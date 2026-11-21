package kbtu.hw2.library.ui.form

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.domain.BusinessRuleException
import kbtu.hw2.library.ui.common.EventfulViewModel
import kbtu.hw2.library.ui.common.UiEvent
import kbtu.hw2.library.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookFormViewModel(
    private val repository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
) : EventfulViewModel() {

    /** Id of the book being edited, or null when creating a new one. */
    private val editingId: String? = savedStateHandle.get<String>(Routes.ARG_BOOK_ID)

    private val _form = MutableStateFlow(BookFormState(isEditMode = editingId != null))
    val form: StateFlow<BookFormState> = _form.asStateFlow()

    init {
        editingId?.let { id ->
            viewModelScope.launch {
                repository.observeBook(id).first()?.let { book ->
                    _form.value = book.toFormState()
                }
            }
        }
    }

    fun onIdChange(value: String) = edit { it.copy(id = value) }
    fun onTitleChange(value: String) = edit { it.copy(title = value) }
    fun onAuthorChange(value: String) = edit { it.copy(author = value) }
    fun onGenresChange(value: String) = edit { it.copy(genres = value) }
    fun onCoverUrlChange(value: String) = edit { it.copy(coverUrl = value) }
    fun onPagesChange(value: String) = edit { it.copy(pages = value.filter { c -> c.isDigit() }) }
    fun onDigitalChange(value: Boolean) = edit { it.copy(isDigital = value) }

    fun save() {
        val current = _form.value
        if (current.isSaving) return

        val errors = BookFormValidator.validate(current)
        if (errors.hasErrors) {
            _form.update { it.copy(errors = errors) }
            return
        }

        viewModelScope.launch {
            _form.update { it.copy(isSaving = true) }
            try {
                val id = editingId ?: current.id.trim()
                if (editingId == null && repository.bookExists(id)) {
                    _form.update {
                        it.copy(isSaving = false, errors = BookFormErrors(id = "This id is already used"))
                    }
                    return@launch
                }
                val book = Book(
                    id = id,
                    title = current.title.trim(),
                    author = current.author.trim(),
                    genres = BookFormValidator.parseGenres(current.genres),
                    pages = if (current.isDigital) 0 else current.pages.trim().toInt(),
                    isDigital = current.isDigital,
                    available = true, // availability is derived from loans, not stored
                    coverUrl = current.coverUrl.trim().ifEmpty { null },
                )
                if (editingId == null) repository.createBook(book) else repository.updateBook(book)
                emit(UiEvent.Message(if (editingId == null) "Book added." else "Changes saved."))
                emit(UiEvent.NavigateBack)
            } catch (e: BusinessRuleException) {
                _form.update { it.copy(isSaving = false) }
                emit(UiEvent.Message(e.message ?: "Could not save the book."))
            }
        }
    }

    /** Any edit clears previous validation errors; they reappear on the next save attempt. */
    private fun edit(transform: (BookFormState) -> BookFormState) {
        _form.update { transform(it).copy(errors = BookFormErrors()) }
    }

    private fun Book.toFormState() = BookFormState(
        isEditMode = true,
        id = id,
        title = title,
        author = author,
        genres = genres.sorted().joinToString(", "),
        coverUrl = coverUrl.orEmpty(),
        pages = if (isDigital) "" else pages.toString(),
        isDigital = isDigital,
    )
}
