open class LibraryItem(
    open val id: String,
    open val title: String,
    open val author: String,
    open val genres: Set<String>,
    open var available: Boolean,
)
