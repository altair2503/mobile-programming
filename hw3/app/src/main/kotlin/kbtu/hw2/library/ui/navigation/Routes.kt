package kbtu.hw2.library.ui.navigation

object Routes {
    const val LIBRARY = "library"
    const val LOANS = "loans"
    const val NEW_BOOK = "new-book"
    const val ARG_BOOK_ID = "bookId"
    const val BOOK = "book/{bookId}"
    const val EDIT_BOOK = "edit-book/{bookId}"

    fun book(id: String) = "book/$id"
    fun editBook(id: String) = "edit-book/$id"
}
