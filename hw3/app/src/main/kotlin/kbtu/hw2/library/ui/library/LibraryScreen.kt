package kbtu.hw2.library.ui.library

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kbtu.hw2.library.ui.common.CollectUiEvents
import kbtu.hw2.library.ui.components.BookCard
import kbtu.hw2.library.ui.components.BorrowDialog
import kbtu.hw2.library.ui.components.ConfirmDialog
import kbtu.hw2.library.ui.components.EmptyState
import kbtu.hw2.library.ui.components.LibrarySearchField
import kbtu.hw2.library.ui.components.StatsSummaryRow
import kbtu.hw2.library.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenBook: (String) -> Unit,
    onAddBook: () -> Unit,
    onOpenLoans: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectUiEvents(viewModel.events)

    // UI-only state (survives rotation, not stored in the ViewModel).
    var statsExpanded by rememberSaveable { mutableStateOf(true) }
    var borrowTargetId by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteTargetId by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Library Catalog") },
                actions = {
                    IconButton(
                        onClick = viewModel::syncFromRemote,
                        enabled = !uiState.isSyncing,
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync remote")
                    }
                    IconButton(onClick = onOpenLoans) {
                        Icon(Icons.Default.List, contentDescription = "Loans")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddBook,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add book") },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // The whole screen scrolls: header, filters and list share one LazyColumn,
            // so nothing is clipped on small or landscape screens.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = Dimens.screenPadding,
                    end = Dimens.screenPadding,
                    top = Dimens.cardSpacing,
                    bottom = 88.dp, // keeps the last card clear of the "Add book" button
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
            ) {
                item(key = "header:stats-title") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Statistics",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TextButton(onClick = { statsExpanded = !statsExpanded }) {
                            Text(if (statsExpanded) "Hide" else "Show")
                        }
                    }
                }

                if (statsExpanded) {
                    item(key = "header:stats") {
                        StatsSummaryRow(stats = uiState.stats)
                    }
                }

                item(key = "header:search") {
                    LibrarySearchField(
                        value = uiState.query,
                        onValueChange = viewModel::onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if (uiState.genres.isNotEmpty()) {
                    item(key = "header:genres") {
                        GenreFilterRow(
                            genres = uiState.genres,
                            selectedGenre = uiState.selectedGenre,
                            onAllClick = viewModel::onAllGenresClick,
                            onGenreClick = viewModel::onGenreClick,
                        )
                    }
                }

                when {
                    uiState.isCatalogEmpty -> item(key = "state:empty") {
                        EmptyState(
                            title = "No books yet",
                            message = "Tap \"Add book\" or use the sync button in the top bar.",
                        )
                    }
                    uiState.books.isEmpty() -> item(key = "state:no-matches") {
                        EmptyState(
                            title = "No matches",
                            message = "No books match your search or genre filter.",
                        )
                    }
                    else -> items(uiState.books, key = { it.id }) { book ->
                        BookCard(
                            book = book,
                            onClick = { onOpenBook(book.id) },
                            onBorrow = { borrowTargetId = book.id },
                            onReturn = { viewModel.returnBook(book.id) },
                            onDelete = { deleteTargetId = book.id },
                        )
                    }
                }
            }

            if (uiState.isSyncing) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    borrowTargetId?.let { id ->
        uiState.books.firstOrNull { it.id == id }?.let { book ->
            BorrowDialog(
                bookTitle = book.title,
                onConfirm = { name ->
                    viewModel.borrow(book.id, name)
                    borrowTargetId = null
                },
                onDismiss = { borrowTargetId = null },
            )
        }
    }

    deleteTargetId?.let { id ->
        uiState.books.firstOrNull { it.id == id }?.let { book ->
            ConfirmDialog(
                title = "Delete \"${book.title}\"?",
                text = if (book.available) {
                    "The book and its loan history will be permanently removed."
                } else {
                    "This book is currently borrowed. Return it before deleting."
                },
                confirmLabel = "Delete",
                confirmEnabled = book.available,
                onConfirm = {
                    viewModel.deleteBook(book.id)
                    deleteTargetId = null
                },
                onDismiss = { deleteTargetId = null },
            )
        }
    }
}

@Composable
private fun GenreFilterRow(
    genres: Set<String>,
    selectedGenre: String?,
    onAllClick: () -> Unit,
    onGenreClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedGenre == null,
            onClick = onAllClick,
            label = { Text("All genres") },
        )
        genres.sorted().forEach { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreClick(genre) },
                label = { Text(genre) },
            )
        }
    }
}
