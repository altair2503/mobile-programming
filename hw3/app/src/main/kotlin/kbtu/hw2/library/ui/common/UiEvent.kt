package kbtu.hw2.library.ui.common

/** One-off events from ViewModels (snackbar messages, navigation). */
sealed interface UiEvent {
    data class Message(val text: String) : UiEvent
    data object NavigateBack : UiEvent
}
