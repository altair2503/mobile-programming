package kbtu.hw2.library.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query(
        """
        SELECT books.*,
               (SELECT COUNT(*) FROM loans WHERE loans.bookId = books.id AND loans.returnedAt IS NULL) AS activeLoans
        FROM books
        ORDER BY books.title COLLATE NOCASE
        """,
    )
    fun observeAll(): Flow<List<BookWithStatus>>

    @Query(
        """
        SELECT books.*,
               (SELECT COUNT(*) FROM loans WHERE loans.bookId = books.id AND loans.returnedAt IS NULL) AS activeLoans
        FROM books
        WHERE books.id = :id
        """,
    )
    fun observeById(id: String): Flow<BookWithStatus?>

    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getById(id: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(book: BookEntity)

    @Update
    suspend fun update(book: BookEntity): Int

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteById(id: String): Int
}
