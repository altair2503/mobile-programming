package kbtu.hw2.library.domain

const val DAY_MILLIS: Long = 24L * 60 * 60 * 1000

object LoanPolicy {
    const val LOAN_PERIOD_DAYS: Int = 14
    const val LOAN_PERIOD_MILLIS: Long = LOAN_PERIOD_DAYS * DAY_MILLIS
    const val MAX_RENEWALS: Int = 2
}

enum class LoanStatus { ACTIVE, OVERDUE, RETURNED }

data class Loan(
    val id: Long,
    val bookId: String,
    val bookTitle: String,
    val borrowerName: String,
    val borrowedAt: Long,
    val dueAt: Long,
    val returnedAt: Long?,
    val renewCount: Int,
    val bookCoverUrl: String? = null,
) {
    fun statusAt(now: Long): LoanStatus = when {
        returnedAt != null -> LoanStatus.RETURNED
        now > dueAt -> LoanStatus.OVERDUE
        else -> LoanStatus.ACTIVE
    }

    val canExtend: Boolean
        get() = returnedAt == null && renewCount < LoanPolicy.MAX_RENEWALS
}

/** Whole days (rounded up) between the due date and the return date; 0 if returned on time. */
fun lateDays(dueAt: Long, returnedAt: Long): Int =
    if (returnedAt <= dueAt) 0 else ((returnedAt - dueAt + DAY_MILLIS - 1) / DAY_MILLIS).toInt()
