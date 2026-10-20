package kbtu.hw2.library.domain

fun Book.toLibraryItem(): LibraryItem =
    if (isDigital) {
        EBook(id, title, author, genres, available, fileSizeMb = 2.5)
    } else {
        PrintedBook(id, title, author, genres, available, pages)
    }
