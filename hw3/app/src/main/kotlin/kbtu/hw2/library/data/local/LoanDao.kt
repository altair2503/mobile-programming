package kbtu.hw2.library.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Query(
        """
        SELECT loans.*, books.title AS bookTitle, books.coverUrl AS bookCoverUrl
        FROM loans INNER JOIN books ON books.id = loans.bookId
        ORDER BY loans.borrowedAt DESC
        """,
    )
    fun observeAll(): Flow<List<LoanWithBook>>

    @Query(
        """
        SELECT loans.*, books.title AS bookTitle, books.coverUrl AS bookCoverUrl
        FROM loans INNER JOIN books ON books.id = loans.bookId
        WHERE loans.bookId = :bookId
        ORDER BY loans.borrowedAt DESC
        """,
    )
    fun observeByBook(bookId: String): Flow<List<LoanWithBook>>

    @Query("SELECT * FROM loans WHERE bookId = :bookId AND returnedAt IS NULL LIMIT 1")
    suspend fun getActiveLoan(bookId: String): LoanEntity?

    @Insert
    suspend fun insert(loan: LoanEntity): Long

    @Update
    suspend fun update(loan: LoanEntity): Int
}
