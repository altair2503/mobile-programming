package kbtu.hw2.library.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.domain.Loan
import kbtu.hw2.library.domain.LoanPolicy
import kbtu.hw2.library.ui.common.EventfulViewModel
import kbtu.hw2.library.ui.navigation.Routes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class BookDetailUiState(
    val book: Book? = null,
    val loans: List<Loan> = emptyList(),
    val isLoading: Boolean = true,
)

class BookDetailViewModel(
    private val repository: LibraryRepository,
    savedStateHandle: SavedStateHandle,
) : EventfulViewModel() {

    private val bookId: String = checkNotNull(savedStateHandle.get<String>(Routes.ARG_BOOK_ID))

    val uiState: StateFlow<BookDetailUiState> = combine(
        repository.observeBook(bookId),
        repository.observeLoansOf(bookId),
    ) { book, loans ->
        BookDetailUiState(book = book, loans = loans, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookDetailUiState())

    fun borrow(borrowerName: String) = perform {
        repository.borrowBook(bookId, borrowerName)
        "Borrowed. Due in ${LoanPolicy.LOAN_PERIOD_DAYS} days."
    }

    fun returnBook() = perform {
        val lateDays = repository.returnBook(bookId)
        if (lateDays > 0) "Returned $lateDays day(s) late." else "Returned."
    }

    fun extendLoan() = perform {
        repository.extendLoan(bookId)
        "Due date extended by ${LoanPolicy.LOAN_PERIOD_DAYS} days."
    }
}
