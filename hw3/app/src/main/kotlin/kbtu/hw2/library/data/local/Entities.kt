package kbtu.hw2.library.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    /** Comma-separated genres. */
    val genres: String,
    val pages: Int,
    val isDigital: Boolean,
    /** Web URL of the cover image, or null. */
    val coverUrl: String? = null,
)

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("bookId")],
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: String,
    val borrowerName: String,
    val borrowedAt: Long,
    val dueAt: Long,
    val returnedAt: Long? = null,
    val renewCount: Int = 0,
)
