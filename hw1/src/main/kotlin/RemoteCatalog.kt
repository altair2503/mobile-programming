import kotlinx.coroutines.delay

object RemoteCatalog {
    suspend fun sync(catalog: LibraryCatalog): LoadResult {
        delay(400)
        return try {
            val newBooks = listOf(
                Book(
                    id = "3",
                    title = "Clean Code",
                    author = "Robert Martin",
                    genres = setOf("Programming", "Software"),
                    pages = 464,
                    available = true,
                    isDigital = false,
                ),
                Book(
                    id = "4",
                    title = "Kotlin in Action",
                    author = "Jemerov",
                    genres = setOf("Programming", "Kotlin"),
                    pages = 0,
                    available = true,
                    isDigital = true,
                ),
            )
            var added = 0
            for (book in newBooks) {
                if (catalog.findById(book.id) == null) {
                    catalog.add(book)
                    added++
                }
            }
            LoadResult.Success(added)
        } catch (e: Exception) {
            LoadResult.Failure(e.message ?: "Unknown error")
        }
    }
}
