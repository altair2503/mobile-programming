package kbtu.hw2.library.ui.form

object BookFormValidator {
    const val TITLE_MAX = 120
    const val AUTHOR_MAX = 80
    const val GENRE_MAX = 30
    const val MAX_GENRES = 5
    const val COVER_URL_MAX = 500
    const val PAGES_MAX = 5000

    private val idPattern = Regex("^[A-Za-z0-9_-]{1,20}$")

    fun validate(form: BookFormState): BookFormErrors {
        val pages = form.pages.trim().toIntOrNull()
        val genres = parseGenres(form.genres)
        return BookFormErrors(
            id = when {
                form.isEditMode -> null
                form.id.isBlank() -> "Id is required"
                !idPattern.matches(form.id.trim()) -> "Use 1–20 letters, digits, '-' or '_'"
                else -> null
            },
            title = when {
                form.title.isBlank() -> "Title is required"
                form.title.trim().length > TITLE_MAX -> "At most $TITLE_MAX characters"
                else -> null
            },
            author = when {
                form.author.isBlank() -> "Author is required"
                form.author.trim().length > AUTHOR_MAX -> "At most $AUTHOR_MAX characters"
                else -> null
            },
            genres = when {
                genres.isEmpty() -> "Enter at least one genre"
                genres.size > MAX_GENRES -> "At most $MAX_GENRES genres"
                genres.any { it.length > GENRE_MAX } -> "Each genre: max $GENRE_MAX characters"
                else -> null
            },
            coverUrl = validateCoverUrl(form.coverUrl.trim()),
            pages = when {
                form.isDigital -> null
                form.pages.isBlank() -> "Pages are required for print books"
                pages == null -> "Enter a whole number"
                pages !in 1..PAGES_MAX -> "Pages must be between 1 and $PAGES_MAX"
                else -> null
            },
        )
    }

    /** Cover is optional: an empty value is valid. */
    private fun validateCoverUrl(url: String): String? = when {
        url.isEmpty() -> null
        url.length > COVER_URL_MAX -> "At most $COVER_URL_MAX characters"
        !(url.startsWith("https://") || url.startsWith("http://")) -> "Must start with https:// or http://"
        url.any { it.isWhitespace() } -> "URL must not contain spaces"
        else -> null
    }

    fun parseGenres(raw: String): Set<String> =
        raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
}
