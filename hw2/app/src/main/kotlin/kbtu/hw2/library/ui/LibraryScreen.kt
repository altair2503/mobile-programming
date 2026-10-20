package kbtu.hw2.library.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kbtu.hw2.library.ui.components.AddBookDialog
import kbtu.hw2.library.ui.components.AddBookFormState
import kbtu.hw2.library.ui.components.BookCard
import kbtu.hw2.library.ui.components.LibrarySearchField
import kbtu.hw2.library.ui.components.StatsSummaryRow
import kbtu.hw2.library.ui.theme.Dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // rememberSaveable: survives configuration changes (rotation) and process death.
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedGenre by rememberSaveable { mutableStateOf<String?>(null) }

    // remember: transient UI only; reset after rotation.
    var showAddDialog by remember { mutableStateOf(false) }
    var statsExpanded by remember { mutableStateOf(true) }
    var addForm by remember { mutableStateOf(AddBookFormState()) }

    val filteredItems by remember {
        derivedStateOf {
            uiState.items.filter { item ->
                val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.author.contains(searchQuery, ignoreCase = true)
                val matchesGenre = selectedGenre == null ||
                    item.genres.any { it.equals(selectedGenre, ignoreCase = true) }
                matchesSearch && matchesGenre
            }
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Library Catalog") },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
            ) {
                Text(
                    text = "Your campus library",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

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
                if (statsExpanded) {
                    StatsSummaryRow(stats = uiState.stats)
                }

                LibrarySearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (uiState.genres.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = selectedGenre == null,
                            onClick = { selectedGenre = null },
                            label = { Text("All genres") },
                        )
                        uiState.genres.sorted().forEach { genre ->
                            FilterChip(
                                selected = selectedGenre == genre,
                                onClick = {
                                    selectedGenre = if (selectedGenre == genre) null else genre
                                },
                                label = { Text(genre) },
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            addForm = AddBookFormState()
                            showAddDialog = true
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Add book")
                    }
                    Button(
                        onClick = { viewModel.syncFromRemote() },
                        enabled = !uiState.isSyncing,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Sync remote")
                    }
                }

                if (filteredItems.isEmpty()) {
                    Text(
                        text = "No books match your filters.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
                    ) {
                        items(filteredItems, key = { it.id }) { book ->
                            BookCard(
                                book = book,
                                onBorrow = { viewModel.borrow(book.id) },
                                onReturn = { viewModel.returnItem(book.id) },
                            )
                        }
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

    if (showAddDialog) {
        AddBookDialog(
            form = addForm,
            onFormChange = { addForm = it },
            onDismiss = { showAddDialog = false },
            onConfirm = { book ->
                viewModel.addBook(book)
                showAddDialog = false
                addForm = AddBookFormState()
            },
        )
    }
}
