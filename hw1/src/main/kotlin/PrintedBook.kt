class PrintedBook(
    id: String,
    title: String,
    author: String,
    available: Boolean,
    val pages: Int,
) : LibraryItem(id, title, author, available), Borrowable {

    override fun borrow(): Boolean {
        if (!available) return false
        available = false
        return true
    }

    override fun describe(): String =
        "[Print] $title by $author ($pages pages) — ${if (available) "available" else "borrowed"}"
}
