package kbtu.hw2.library.ui.library

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.data.remote.RemoteCatalog
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.ui.common.EventfulViewModel
import kbtu.hw2.library.ui.common.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val repository: LibraryRepository,
    private val savedStateHandle: SavedStateHandle,
) : EventfulViewModel() {

    private val isSyncing = MutableStateFlow(false)

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.observeBooks(),
        savedStateHandle.getStateFlow(KEY_QUERY, ""),
        savedStateHandle.getStateFlow<String?>(KEY_GENRE, null),
        isSyncing,
    ) { books, query, genre, syncing ->
        val visible = books.filter { book -> matches(book, query, genre) }
        val genres = books.flatMap { it.genres }.toSet()
        LibraryUiState(
            books = visible,
            genres = genres,
            query = query,
            selectedGenre = genre,
            stats = LibraryStats(
                bookCount = books.size,
                totalPrintPages = books.filter { !it.isDigital }.sumOf { it.pages },
                genreCount = genres.size,
            ),
            isCatalogEmpty = books.isEmpty(),
            isSyncing = syncing,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())

    fun onQueryChange(query: String) {
        savedStateHandle[KEY_QUERY] = query
    }

    fun onGenreClick(genre: String) {
        val current = savedStateHandle.get<String>(KEY_GENRE)
        savedStateHandle[KEY_GENRE] = if (current == genre) null else genre
    }

    fun onAllGenresClick() {
        savedStateHandle[KEY_GENRE] = null
    }

    fun borrow(bookId: String, borrowerName: String) = perform {
        repository.borrowBook(bookId, borrowerName)
        "Borrowed by $borrowerName."
    }

    fun returnBook(bookId: String) = perform {
        val lateDays = repository.returnBook(bookId)
        if (lateDays > 0) "Returned $lateDays day(s) late." else "Returned."
    }

    fun deleteBook(bookId: String) = perform {
        repository.deleteBook(bookId)
        "Book deleted."
    }

    fun syncFromRemote() {
        if (isSyncing.value) return
        viewModelScope.launch {
            isSyncing.value = true
            try {
                val added = repository.importBooks(RemoteCatalog.fetchBooks())
                emit(
                    UiEvent.Message(
                        if (added > 0) "Added $added book(s) from remote." else "Remote catalog is already up to date.",
                    ),
                )
            } catch (e: Exception) {
                emit(UiEvent.Message("Sync failed: ${e.message}"))
            } finally {
                isSyncing.value = false
            }
        }
    }

    private fun matches(book: Book, query: String, genre: String?): Boolean {
        val matchesQuery = query.isBlank() ||
            book.title.contains(query, ignoreCase = true) ||
            book.author.contains(query, ignoreCase = true)
        val matchesGenre = genre == null || book.genres.any { it.equals(genre, ignoreCase = true) }
        return matchesQuery && matchesGenre
    }

    private companion object {
        const val KEY_QUERY = "query"
        const val KEY_GENRE = "genre"
    }
}
