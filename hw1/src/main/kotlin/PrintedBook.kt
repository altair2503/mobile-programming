class PrintedBook(
    id: String,
    title: String,
    author: String,
    genres: Set<String>,
    available: Boolean,
    val pages: Int,
) : LibraryItem(id, title, author, genres, available), Borrowable {

    override fun borrow(): Boolean {
        if (!available) return false
        available = false
        return true
    }

    override fun returnItem(): Boolean {
        if (available) return false
        available = true
        return true
    }

    override fun describe(): String =
        "[Print] $title by $author ($pages pages) — ${if (available) "available" else "borrowed"}"
}
