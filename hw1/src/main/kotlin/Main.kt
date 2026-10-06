import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val catalog = LibraryCatalog()

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

    var running = true
    while (running) {
        println()
        println("=== Library ===")
        println("1) List all books")
        println("2) Filter by genre")
        println("3) Stats (total pages of print books)")
        println("4) Show borrowables (polymorphism)")
        println("5) Sync from remote catalog")
        println("0) Exit")
        print("Choice: ")

        val input = readlnOrNull()?.trim()
        if (input == null) {
            println("Input closed.")
            break
        }
        val choice = input

        when (choice) {
            "1" -> {
                if (catalog.size() == 0) {
                    println("Catalog is empty.")
                } else {
                    catalog.printAll { book ->
                        val tags = book.genres.joinToString(", ")
                        "${book.id}: ${book.title} [$tags]"
                    }
                }
            }
            "2" -> {
                print("Genre: ")
                val genre = readlnOrNull()?.trim().orEmpty()
                if (genre.isEmpty()) {
                    println("Genre cannot be empty.")
                } else {
                    val titles = catalog.titlesByGenre(genre)
                    if (titles.isEmpty()) {
                        println("No books found.")
                    } else {
                        titles.forEach { println("- $it") }
                    }
                }
            }
            "3" -> {
                val count = catalog.size()
                if (count == 0) {
                    println("No books yet.")
                } else {
                    val total = catalog.totalPages()
                    val genres = catalog.allGenres()
                    println("Books: $count, print pages total: $total")
                    println("Genres in catalog: ${genres.joinToString(", ")}")
                }
            }
            "4" -> {
                val items: List<Borrowable> = catalog.borrowables()
                for (item in items) {
                    println(item.describe())
                    if (item.available) {
                        val ok: Boolean = item.borrow()
                        println(if (ok) "  -> borrowed" else "  -> failed")
                    }
                }
            }
            "5" -> {
                println("Syncing...")
                when (val result = RemoteCatalog.sync(catalog)) {
                    is LoadResult.Success -> println("Added ${result.added} book(s).")
                    is LoadResult.Failure -> println("Sync failed: ${result.message}")
                }
            }
            "0" -> running = false
            else -> println("Unknown option.")
        }
    }

    println("Goodbye.")
}
