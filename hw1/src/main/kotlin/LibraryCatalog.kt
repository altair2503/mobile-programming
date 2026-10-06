class LibraryCatalog {
    private val items: MutableList<LibraryItem> = mutableListOf()
    private val byId: MutableMap<String, LibraryItem> = mutableMapOf()

    fun add(dto: Book) {
        if (byId.containsKey(dto.id)) return
        val item = dto.toLibraryItem()
        items.add(item)
        byId[item.id] = item
    }

    fun findById(id: String): LibraryItem? = byId[id]

    fun allGenres(): Set<String> =
        items.flatMap { it.genres }.toSet()

    fun printAll(formatter: (LibraryItem) -> String = { "${it.id}: ${it.title} (${it.author})" }) {
        items.sortedBy { it.title }.forEach { println(formatter(it)) }
    }

    fun titlesByGenre(genre: String): List<String> =
        items
            .filter { item -> item.genres.any { it.equals(genre, ignoreCase = true) } }
            .map { it.title }

    fun totalPages(): Int {
        val pages = items.filterIsInstance<PrintedBook>().map { it.pages }
        return if (pages.isEmpty()) 0 else pages.reduce { sum, p -> sum + p }
    }

    fun borrowables(): List<Borrowable> =
        items.filterIsInstance<Borrowable>()

    fun borrowById(id: String): Boolean {
        val item = byId[id] as? Borrowable ?: return false
        return item.borrow()
    }

    fun returnById(id: String): Boolean {
        val item = byId[id] as? Borrowable ?: return false
        return item.returnItem()
    }

    fun size(): Int = items.size
}
