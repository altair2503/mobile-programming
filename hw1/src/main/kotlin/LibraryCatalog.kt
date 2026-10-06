class LibraryCatalog {
    private val books: MutableList<Book> = mutableListOf()
    private val byId: MutableMap<String, Book> = mutableMapOf()

    fun add(book: Book) {
        books.add(book)
        byId[book.id] = book
    }

    fun findById(id: String): Book? = byId[id]

    fun allGenres(): Set<String> =
        books.flatMap { it.genres }.toSet()

    fun printAll(formatter: (Book) -> String = { "${it.id}: ${it.title} (${it.author})" }) {
        books.sortedBy { it.title }.forEach { println(formatter(it)) }
    }

    fun titlesByGenre(genre: String): List<String> =
        books
            .filter { book -> book.genres.any { it.equals(genre, ignoreCase = true) } }
            .map { it.title }

    fun totalPages(): Int {
        val pages = books.filter { !it.isDigital }.map { it.pages }
        return if (pages.isEmpty()) 0 else pages.reduce { sum, p -> sum + p }
    }

    fun borrowables(): List<Borrowable> =
        books.map { book ->
            if (book.isDigital) {
                EBook(book.id, book.title, book.author, book.available, fileSizeMb = 2.5)
            } else {
                PrintedBook(book.id, book.title, book.author, book.available, book.pages)
            }
        }

    fun size(): Int = books.size
}
