package kbtu.hw2.library.data.remote

import kbtu.hw2.library.domain.Book
import kotlinx.coroutines.delay

/** Fake remote API (same data as HW1/HW2, plus cover URLs). */
object RemoteCatalog {
    suspend fun fetchBooks(): List<Book> {
        delay(400)
        return listOf(
            Book(
                id = "3",
                title = "Clean Code",
                author = "Robert Martin",
                genres = setOf("Programming", "Software"),
                pages = 464,
                isDigital = false,
                available = true,
                coverUrl = "https://m.media-amazon.com/images/I/71nj3JM-igL._AC_UF1000,1000_QL80_.jpg",
            ),
            Book(
                id = "4",
                title = "Kotlin in Action",
                author = "Jemerov",
                genres = setOf("Programming", "Kotlin"),
                pages = 0,
                isDigital = true,
                available = true,
                coverUrl = "https://covers.openlibrary.org/b/isbn/9781617293290-L.jpg",
            ),
        )
    }
}
