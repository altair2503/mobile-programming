package kbtu.hw2.library.data.local

import kbtu.hw2.library.domain.Book
import kbtu.hw2.library.domain.Loan

internal fun BookWithStatus.toDomain(): Book = Book(
    id = book.id,
    title = book.title,
    author = book.author,
    genres = book.genres.toGenreSet(),
    pages = book.pages,
    isDigital = book.isDigital,
    available = activeLoans == 0,
    coverUrl = book.coverUrl,
)

internal fun Book.toEntity(): BookEntity = BookEntity(
    id = id,
    title = title,
    author = author,
    genres = genres.sorted().joinToString(","),
    pages = if (isDigital) 0 else pages,
    isDigital = isDigital,
    coverUrl = coverUrl,
)

internal fun LoanWithBook.toDomain(): Loan = Loan(
    id = loan.id,
    bookId = loan.bookId,
    bookTitle = bookTitle,
    borrowerName = loan.borrowerName,
    borrowedAt = loan.borrowedAt,
    dueAt = loan.dueAt,
    returnedAt = loan.returnedAt,
    renewCount = loan.renewCount,
    bookCoverUrl = bookCoverUrl,
)

private fun String.toGenreSet(): Set<String> =
    split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
