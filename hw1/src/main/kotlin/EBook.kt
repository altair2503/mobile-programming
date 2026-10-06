class EBook(
    id: String,
    title: String,
    author: String,
    available: Boolean,
    val fileSizeMb: Double,
) : LibraryItem(id, title, author, available), Borrowable {

    override fun borrow(): Boolean {
        if (!available) return false
        available = false
        return true
    }

    override fun describe(): String =
        "[E-Book] $title by $author (${fileSizeMb} MB) — ${if (available) "available" else "borrowed"}"
}
