package kbtu.hw2.library.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** Book row plus the number of its currently open loans (computed in SQL). */
data class BookWithStatus(
    @Embedded val book: BookEntity,
    @ColumnInfo(name = "activeLoans") val activeLoans: Int,
)

/** Loan row plus the title and cover of the borrowed book. */
data class LoanWithBook(
    @Embedded val loan: LoanEntity,
    @ColumnInfo(name = "bookTitle") val bookTitle: String,
    @ColumnInfo(name = "bookCoverUrl") val bookCoverUrl: String?,
)
