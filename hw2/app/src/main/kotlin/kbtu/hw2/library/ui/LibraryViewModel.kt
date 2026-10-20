package kbtu.hw2.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.domain.EBook
import kbtu.hw2.library.domain.LibraryCatalog
import kbtu.hw2.library.domain.LibraryItem
import kbtu.hw2.library.domain.LoadResult
import kbtu.hw2.library.domain.PrintedBook
import kbtu.hw2.library.domain.RemoteCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val catalog = LibraryCatalog()

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        seedCatalog()
        refreshFromCatalog()
    }

    private fun seedCatalog() {
        catalog.add(
            Book(
                id = "1",
                title = "1984",
                author = "George Orwell",
                genres = setOf("Fiction", "Dystopia"),
                pages = 328,
                available = true,
            ),
        )
        catalog.add(
            Book(
                id = "2",
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                genres = setOf("Fiction", "Fantasy"),
                pages = 310,
                available = true,
            ),
        )
    }

    fun borrow(id: String) {
        when {
            catalog.findById(id) == null -> showMessage("Book not found.")
            catalog.borrowById(id) -> {
                refreshFromCatalog()
                showMessage("Borrowed.")
            }
            else -> showMessage("Could not borrow (already borrowed?).")
        }
    }

    fun returnItem(id: String) {
        when {
            catalog.findById(id) == null -> showMessage("Book not found.")
            catalog.returnById(id) -> {
                refreshFromCatalog()
                showMessage("Returned.")
            }
            else -> showMessage("Could not return (already available?).")
        }
    }

    fun addBook(book: Book) {
        if (catalog.findById(book.id) != null) {
            showMessage("A book with this id already exists.")
            return
        }
        catalog.add(book)
        refreshFromCatalog()
        showMessage("Added \"${book.title}\".")
    }

    fun syncFromRemote() {
        if (_uiState.value.isSyncing) return
        _uiState.update { it.copy(isSyncing = true) }
        viewModelScope.launch {
            when (val result = RemoteCatalog.sync(catalog)) {
                is LoadResult.Success -> {
                    refreshFromCatalog()
                    showMessage(
                        if (result.added > 0) {
                            "Added ${result.added} book(s) from remote."
                        } else {
                            "Remote catalog is already up to date."
                        },
                    )
                }
                is LoadResult.Failure -> showMessage("Sync failed: ${result.message}")
            }
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }

    private fun refreshFromCatalog() {
        val items = catalog.allItems().map { it.toBookListItem() }
        val genres = catalog.allGenres()
        _uiState.update {
            it.copy(
                items = items,
                genres = genres,
                stats = LibraryStats(
                    bookCount = catalog.size(),
                    totalPrintPages = catalog.totalPages(),
                    genreCount = genres.size,
                ),
            )
        }
    }

    private fun LibraryItem.toBookListItem(): BookListItem {
        val (typeLabel, detail) = when (this) {
            is PrintedBook -> "Print" to "$pages pages"
            is EBook -> "E-Book" to "${fileSizeMb} MB"
            else -> "Item" to ""
        }
        return BookListItem(
            id = id,
            title = title,
            author = author,
            genres = genres,
            available = available,
            typeLabel = typeLabel,
            detail = detail,
        )
    }
}
