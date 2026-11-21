package kbtu.hw2.library.ui.form

data class BookFormErrors(
    val id: String? = null,
    val title: String? = null,
    val author: String? = null,
    val genres: String? = null,
    val coverUrl: String? = null,
    val pages: String? = null,
) {
    val hasErrors: Boolean
        get() = listOf(id, title, author, genres, coverUrl, pages).any { it != null }
}

data class BookFormState(
    val isEditMode: Boolean = false,
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val genres: String = "",
    val coverUrl: String = "",
    val pages: String = "",
    val isDigital: Boolean = false,
    val errors: BookFormErrors = BookFormErrors(),
    val isSaving: Boolean = false,
)
