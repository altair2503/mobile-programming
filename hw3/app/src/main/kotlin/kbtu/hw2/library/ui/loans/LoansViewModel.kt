package kbtu.hw2.library.ui.loans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.domain.Loan
import kbtu.hw2.library.domain.LoanPolicy
import kbtu.hw2.library.domain.LoanStatus
import kbtu.hw2.library.ui.common.EventfulViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

enum class LoanFilter(val label: String) {
    ALL("All"),
    OPEN("Open"),
    OVERDUE("Overdue"),
    RETURNED("Returned"),
}

data class LoansUiState(
    val loans: List<Loan> = emptyList(),
    val filter: LoanFilter = LoanFilter.OPEN,
    val hasAnyLoans: Boolean = false,
    val openCount: Int = 0,
    val overdueCount: Int = 0,
    val returnedCount: Int = 0,
    val now: Long = 0L,
)

class LoansViewModel(
    private val repository: LibraryRepository,
    private val savedStateHandle: SavedStateHandle,
) : EventfulViewModel() {

    val uiState: StateFlow<LoansUiState> = combine(
        repository.observeAllLoans(),
        savedStateHandle.getStateFlow(KEY_FILTER, LoanFilter.OPEN),
    ) { loans, filter ->
        val now = System.currentTimeMillis()
        val withStatus = loans.map { it to it.statusAt(now) }
        val visible = withStatus
            .filter { (_, status) ->
                when (filter) {
                    LoanFilter.ALL -> true
                    LoanFilter.OPEN -> status != LoanStatus.RETURNED
                    LoanFilter.OVERDUE -> status == LoanStatus.OVERDUE
                    LoanFilter.RETURNED -> status == LoanStatus.RETURNED
                }
            }
            .map { it.first }
        LoansUiState(
            loans = visible,
            filter = filter,
            hasAnyLoans = loans.isNotEmpty(),
            openCount = withStatus.count { it.second != LoanStatus.RETURNED },
            overdueCount = withStatus.count { it.second == LoanStatus.OVERDUE },
            returnedCount = withStatus.count { it.second == LoanStatus.RETURNED },
            now = now,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LoansUiState())

    fun setFilter(filter: LoanFilter) {
        savedStateHandle[KEY_FILTER] = filter
    }

    fun returnBook(bookId: String) = perform {
        val lateDays = repository.returnBook(bookId)
        if (lateDays > 0) "Returned $lateDays day(s) late." else "Returned."
    }

    fun extendLoan(bookId: String) = perform {
        repository.extendLoan(bookId)
        "Due date extended by ${LoanPolicy.LOAN_PERIOD_DAYS} days."
    }

    private companion object {
        const val KEY_FILTER = "loanFilter"
    }
}
