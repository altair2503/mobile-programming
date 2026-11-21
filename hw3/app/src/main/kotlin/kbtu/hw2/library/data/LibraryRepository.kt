package kbtu.hw2.library.data

import androidx.room.withTransaction
import kbtu.hw2.library.data.local.BookEntity
import kbtu.hw2.library.data.local.LibraryDatabase
import kbtu.hw2.library.data.local.LoanEntity
import kbtu.hw2.library.data.local.toDomain
import kbtu.hw2.library.data.local.toEntity
import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.domain.BusinessRuleException
import kbtu.hw2.library.domain.Loan
import kbtu.hw2.library.domain.LoanPolicy
import kbtu.hw2.library.domain.lateDays
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Single access point to persistent data. All multi-step changes run inside
 * a Room transaction so that books and loans stay consistent.
 */
class LibraryRepository(
    private val database: LibraryDatabase,
    private val clock: () -> Long = { System.currentTimeMillis() },
) {
    private val bookDao = database.bookDao()
    private val loanDao = database.loanDao()

    // ---- Observe (Room Flow: UI updates automatically on any change) ----

    fun observeBooks(): Flow<List<Book>> =
        bookDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeBook(id: String): Flow<Book?> =
        bookDao.observeById(id).map { row -> row?.toDomain() }

    fun observeAllLoans(): Flow<List<Loan>> =
        loanDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeLoansOf(bookId: String): Flow<List<Loan>> =
        loanDao.observeByBook(bookId).map { rows -> rows.map { it.toDomain() } }

    // ---- Book CRUD ----

    suspend fun bookExists(id: String): Boolean = bookDao.getById(id) != null

    suspend fun createBook(book: Book) {
        database.withTransaction {
            if (bookDao.getById(book.id) != null) {
                throw BusinessRuleException("A book with id \"${book.id}\" already exists.")
            }
            bookDao.insert(book.toEntity())
        }
    }

    suspend fun updateBook(book: Book) {
        database.withTransaction {
            requireBook(book.id)
            bookDao.update(book.toEntity())
        }
    }

    suspend fun deleteBook(id: String) {
        database.withTransaction {
            requireBook(id)
            if (loanDao.getActiveLoan(id) != null) {
                throw BusinessRuleException("Cannot delete a book that is currently borrowed.")
            }
            bookDao.deleteById(id) // loan history is removed by ON DELETE CASCADE
        }
    }

    /** Adds books that are not yet stored; returns how many were added. */
    suspend fun importBooks(books: List<Book>): Int = database.withTransaction {
        var added = 0
        books.forEach { book ->
            if (bookDao.getById(book.id) == null) {
                bookDao.insert(book.toEntity())
                added++
            }
        }
        added
    }

    // ---- Loan workflow: AVAILABLE -> BORROWED (ACTIVE/OVERDUE) -> RETURNED ----

    suspend fun borrowBook(bookId: String, borrowerName: String) {
        database.withTransaction {
            requireBook(bookId)
            if (loanDao.getActiveLoan(bookId) != null) {
                throw BusinessRuleException("This book is already borrowed.")
            }
            val now = clock()
            loanDao.insert(
                LoanEntity(
                    bookId = bookId,
                    borrowerName = borrowerName,
                    borrowedAt = now,
                    dueAt = now + LoanPolicy.LOAN_PERIOD_MILLIS,
                ),
            )
        }
    }

    /** Returns the number of days the book was returned late (0 if on time). */
    suspend fun returnBook(bookId: String): Int = database.withTransaction {
        val loan = loanDao.getActiveLoan(bookId)
            ?: throw BusinessRuleException("This book is not currently borrowed.")
        val now = clock()
        loanDao.update(loan.copy(returnedAt = now))
        lateDays(dueAt = loan.dueAt, returnedAt = now)
    }

    suspend fun extendLoan(bookId: String) {
        database.withTransaction {
            val loan = loanDao.getActiveLoan(bookId)
                ?: throw BusinessRuleException("This book is not currently borrowed.")
            if (loan.renewCount >= LoanPolicy.MAX_RENEWALS) {
                throw BusinessRuleException("Renewal limit reached (${LoanPolicy.MAX_RENEWALS}).")
            }
            val base = maxOf(loan.dueAt, clock())
            loanDao.update(
                loan.copy(
                    dueAt = base + LoanPolicy.LOAN_PERIOD_MILLIS,
                    renewCount = loan.renewCount + 1,
                ),
            )
        }
    }

    private suspend fun requireBook(id: String): BookEntity =
        bookDao.getById(id) ?: throw BusinessRuleException("Book not found.")
}
