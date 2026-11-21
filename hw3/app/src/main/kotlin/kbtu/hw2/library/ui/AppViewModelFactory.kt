package kbtu.hw2.library.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kbtu.hw2.library.LibraryApplication
import kbtu.hw2.library.data.LibraryRepository
import kbtu.hw2.library.ui.detail.BookDetailViewModel
import kbtu.hw2.library.ui.form.BookFormViewModel
import kbtu.hw2.library.ui.library.LibraryViewModel
import kbtu.hw2.library.ui.loans.LoansViewModel

object AppViewModelFactory {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { LibraryViewModel(repository(), createSavedStateHandle()) }
        initializer { BookDetailViewModel(repository(), createSavedStateHandle()) }
        initializer { BookFormViewModel(repository(), createSavedStateHandle()) }
        initializer { LoansViewModel(repository(), createSavedStateHandle()) }
    }
}

private fun CreationExtras.repository(): LibraryRepository =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LibraryApplication).repository
