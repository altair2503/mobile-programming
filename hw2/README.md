# Library Catalog (HW2)

Android application that continues the [HW1](../hw1/) library console project. The same domain model (`LibraryCatalog`, `Book`, borrow/return, remote sync) is presented with **Jetpack Compose**, **Material 3**, and a **ViewModel** for screen state.

## Features

- Browse books in a scrollable list (`LazyColumn` + `Card`)
- **Search** by title or author (`TextField`)
- **Filter by genre** (`FilterChip`)
- **Borrow / return** books (updates catalog state in the ViewModel)
- **Add book** dialog (`TextField`, `Button`, `Checkbox` for digital vs print)
- **Sync from remote** catalog (coroutine + loading overlay), same fake API as HW1
- Statistics row: book count, total print pages, genre count
- Material 3 green theme and book `Image` icons on each card

### Reusable Composables

| Component | Location |
|-----------|----------|
| `BookCard` | `ui/components/BookCard.kt` |
| `LibrarySearchField` | `ui/components/LibrarySearchField.kt` |
| `StatsSummaryRow` | `ui/components/StatsSummaryRow.kt` |
| `AddBookDialog` | `ui/components/AddBookDialog.kt` |

### Requirements mapping

| Requirement | Implementation |
|-------------|----------------|
| Compose + Material 3 | `LibraryScreen`, `LibraryCatalogTheme` |
| Row / Column / Box | Layout in `LibraryScreen`, `BookCard` |
| LazyColumn, Card, Text, Image | `LibraryScreen`, `BookCard` |
| ≥ 2 interactions | Search, genre filter, borrow/return, add, sync |
| ViewModel | `LibraryViewModel` + `StateFlow<LibraryUiState>` |
| `remember` / `rememberSaveable` | `LibraryScreen.kt` |

## Screenshots

Main catalog with seeded books (1984, The Hobbit), search bar:

<img alt="1 - main list.png" src="screenshots/1%20-%20main%20list.png" width="550"/>

Add-book dialog:

<img alt="2 - add book.png" src="screenshots/2%20-%20add%20book.png" width="550"/>

## How to run

**Requirements:** Android Studio, **JDK 21+**, Android SDK (API 34), device or emulator with **API 26+**.

1. Open the `hw2` folder in Android Studio (File → Open).
2. Ensure `local.properties` contains your SDK path, for example:
   ```properties
   sdk.dir=/path/to/Android/sdk
   ```
3. Run the **app** configuration on an emulator or physical device.

From the terminal:

```bash
cd hw2
./gradlew :app:installDebug
```

Then launch **Library Catalog** on the device.

## State management: `remember`, `rememberSaveable`, and `ViewModel`

| Mechanism | What we store | Survives rotation? | Survives process death? |
|-----------|---------------|----------------------|-------------------------|
| **ViewModel** | Catalog items, availability, sync loading, snackbar messages | Yes | No (unless `SavedStateHandle` is added) |
| **rememberSaveable** | Search query, selected genre filter | Yes | Yes (saved in the instance state bundle) |
| **remember** | Add-book dialog visibility, stats section expanded/collapsed | No | No |

**Why this split?**

- **ViewModel** holds business data and logic that must survive configuration changes (e.g. rotation) but is not UI chrome. Borrowing a book updates the catalog in the ViewModel; after rotation the list still shows “Borrowed”.
- **rememberSaveable** keeps user input that should not be lost on rotation—search text and genre selection—without putting every keystroke through the ViewModel.
- **remember** is for ephemeral UI state. Toggling “Hide” on statistics or opening the add dialog resets after rotation, which is acceptable for non-critical UI.

Filtering uses `derivedStateOf` in `LibraryScreen`: catalog data comes from the ViewModel; search and genre come from `rememberSaveable` and are combined in the UI layer.

## Project structure

```
hw2/
  app/src/main/kotlin/kbtu/hw2/library/
    domain/          # Ported from HW1
    ui/              # ViewModel, screen, theme
    ui/components/   # Reusable composables
    MainActivity.kt
```

Domain code lives under `domain/`; Compose UI under `ui/`.

## Relation to HW1

| HW1 (console) | HW2 (Android) |
|---------------|---------------|
| Menu option 1 – list all | `LazyColumn` of `BookCard` |
| Option 2 – filter by genre | Genre `FilterChip`s + `derivedStateOf` |
| Option 3 – stats | `StatsSummaryRow` |
| Options 5–6 – borrow/return | Buttons on each card |
| Option 7 – remote sync | **Sync remote** button |
| — | Search + add book (new UI features) |
