package kbtu.hw2.library.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * One snackbar host for the whole app. Messages are shown from an app-level scope,
 * so they stay visible even when the screen that produced them is popped.
 */
class AppSnackbar(
    val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    fun show(message: String): Job = scope.launch {
        hostState.showSnackbar(message)
    }
}

val LocalAppSnackbar = compositionLocalOf<AppSnackbar> {
    error("AppSnackbar is not provided")
}

/** Collects [UiEvent]s of a screen: shows messages and runs navigation requests. */
@Composable
fun CollectUiEvents(
    events: Flow<UiEvent>,
    onNavigateBack: () -> Unit = {},
) {
    val snackbar = LocalAppSnackbar.current
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is UiEvent.Message -> snackbar.show(event.text)
                UiEvent.NavigateBack -> currentOnNavigateBack()
            }
        }
    }
}
