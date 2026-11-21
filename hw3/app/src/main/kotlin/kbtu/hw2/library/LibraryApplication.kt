package kbtu.hw2.library

import android.app.Application
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.data.local.LibraryDatabase

class LibraryApplication : Application() {
    /** Application-scoped dependency shared by all ViewModels. */
    val repository: LibraryRepository by lazy {
        LibraryRepository(LibraryDatabase.create(this))
    }
}
