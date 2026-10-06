data class Book(
    val id: String,
    val title: String,
    val author: String,
    val genres: Set<String>,
    val pages: Int,
    val available: Boolean,
    val isDigital: Boolean = false,
)
