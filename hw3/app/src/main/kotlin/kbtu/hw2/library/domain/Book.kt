package kbtu.hw2.library.domain

/** Book as seen by the UI. [available] is derived from active loans in the database. */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val genres: Set<String>,
    val pages: Int,
    val isDigital: Boolean,
    val available: Boolean,
    val coverUrl: String? = null,
)
