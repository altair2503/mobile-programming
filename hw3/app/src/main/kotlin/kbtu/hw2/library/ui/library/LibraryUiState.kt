package kbtu.hw2.library.ui.library

import kbtu.hw2.library.domain.Book

data class LibraryStats(
    val bookCount: Int,
    val totalPrintPages: Int,
    val genreCount: Int,
)

data class LibraryUiState(
    /** Books after search and genre filter. */
    val books: List<Book> = emptyList(),
    val genres: Set<String> = emptySet(),
    val query: String = "",
    val selectedGenre: String? = null,
    val stats: LibraryStats = LibraryStats(0, 0, 0),
    /** True only when the whole catalog is empty (not when a filter hides everything). */
    val isCatalogEmpty: Boolean = false,
    val isSyncing: Boolean = false,
)
