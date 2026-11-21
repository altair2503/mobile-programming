package kbtu.hw2.library.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BookEntity::class, LoanEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class LibraryDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun loanDao(): LoanDao

    companion object {
        fun create(context: Context): LibraryDatabase =
            Room.databaseBuilder(context, LibraryDatabase::class.java, "library.db")
                .addCallback(SeedCallback)
                .fallbackToDestructiveMigration()
                .build()
    }
}

/** Initial data inserted when the database file is created. */
private object SeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "INSERT INTO books (id, title, author, genres, pages, isDigital, coverUrl) VALUES " +
                "('1', '1984', 'George Orwell', 'Dystopia,Fiction', 328, 0, " +
                "'https://covers.openlibrary.org/b/isbn/9780451524935-L.jpg')",
        )
        db.execSQL(
            "INSERT INTO books (id, title, author, genres, pages, isDigital, coverUrl) VALUES " +
                "('2', 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy,Fiction', 310, 0, " +
                "'https://covers.openlibrary.org/b/isbn/9780547928227-L.jpg')",
        )
    }
}
