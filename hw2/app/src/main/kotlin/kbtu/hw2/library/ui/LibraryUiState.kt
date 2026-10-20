package kbtu.hw2.library.ui

data class BookListItem(
    val id: String,
    val title: String,
    val author: String,
    val genres: Set<String>,
    val available: Boolean,
    val typeLabel: String,
    val detail: String,
)

data class LibraryStats(
    val bookCount: Int,
    val totalPrintPages: Int,
    val genreCount: Int,
)

data class LibraryUiState(
    val items: List<BookListItem> = emptyList(),
    val genres: Set<String> = emptySet(),
    val stats: LibraryStats = LibraryStats(0, 0, 0),
    val isSyncing: Boolean = false,
    val userMessage: String? = null,
)
