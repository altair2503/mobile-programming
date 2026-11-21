package kbtu.hw2.library.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.ui.common.CollectUiEvents
import kbtu.hw2.library.ui.components.BookCover
import kbtu.hw2.library.ui.components.BorrowDialog
import kbtu.hw2.library.ui.components.EmptyState
import kbtu.hw2.library.ui.components.LoanCard
import kbtu.hw2.library.ui.components.StatusBadge
import kbtu.hw2.library.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CollectUiEvents(viewModel.events)
    var showBorrowDialog by rememberSaveable { mutableStateOf(false) }
    val book = uiState.book
    val now = System.currentTimeMillis()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = book?.title ?: "Book details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (book != null) {
                        IconButton(onClick = { onEdit(book.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit book")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> Unit
            book == null -> EmptyState(
                title = "Book not found",
                message = "This book may have been deleted.",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
            ) {
                item(key = "info") {
                    BookHeader(book = book, onBorrow = { showBorrowDialog = true })
                }
                item(key = "history-title") {
                    Text(
                        text = "Loan history (${uiState.loans.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                if (uiState.loans.isEmpty()) {
                    item(key = "no-loans") {
                        Text(
                            text = "This book has never been borrowed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(uiState.loans, key = { it.id }) { loan ->
                        LoanCard(
                            loan = loan,
                            now = now,
                            onReturn = viewModel::returnBook,
                            onExtend = viewModel::extendLoan,
                        )
                    }
                }
            }
        }
    }

    if (showBorrowDialog && book != null) {
        BorrowDialog(
            bookTitle = book.title,
            onConfirm = { name ->
                viewModel.borrow(name)
                showBorrowDialog = false
            },
            onDismiss = { showBorrowDialog = false },
        )
    }
}

@Composable
private fun BookHeader(
    book: Book,
    onBorrow: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BookCover(
            coverUrl = book.coverUrl,
            title = book.title,
            modifier = Modifier
                .padding(top = 8.dp)
                .width(160.dp)
                .height(240.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
        )
        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusBadge(
                text = if (book.available) "Available" else "Borrowed",
                color = if (book.available) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
            StatusBadge(
                text = if (book.isDigital) "E-Book" else "${book.pages} pages",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = book.genres.sorted().joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (book.available) {
            Button(
                onClick = onBorrow,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Borrow")
            }
        }
    }
}
