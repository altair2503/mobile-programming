# Library Console App (HW1)

A small Kotlin/JVM console application that manages a simple library catalog: list books, filter by genre, show statistics, demonstrate polymorphic borrowables, and sync new books from a fake remote source.

## How to run

Requirements: **JDK 17+**

```bash
cd hw1
./gradlew run
```

In IntelliJ IDEA: open the `hw1` folder as a project and run `Main.kt`.

## Where homework requirements are shown

| Requirement | File |
|-------------|------|
| Variables, types, conditions, loops | `Main.kt` |
| `List`, `Set`, `Map` | `LibraryCatalog.kt`, `Book.kt` |
| `map`, `filter`, `reduce` | `LibraryCatalog.kt` |
| Functions, higher-order functions, lambdas | `LibraryCatalog.kt`, `Main.kt` |
| Classes and objects | `LibraryItem.kt`, `LibraryCatalog.kt`, `RemoteCatalog.kt` (object) |
| Inheritance | `LibraryItem.kt`, `PrintedBook.kt`, `EBook.kt` |
| Interfaces and polymorphism | `Borrowable.kt`, `PrintedBook.kt`, `EBook.kt`, menu option 4 in `Main.kt` |
| Data class | `Book.kt` |
| Sealed class | `LoadResult.kt` |
| Suspend function and coroutine | `RemoteCatalog.kt`, `runBlocking` in `Main.kt` |

Project path in repository: **`hw1/`**
