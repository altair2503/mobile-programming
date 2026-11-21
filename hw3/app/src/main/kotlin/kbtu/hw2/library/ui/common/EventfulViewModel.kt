package kbtu.hw2.library.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kbtu.hw2.library.domain.BusinessRuleException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/** Base ViewModel: runs user actions and reports the result as a [UiEvent]. */
abstract class EventfulViewModel : ViewModel() {

    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = channel.receiveAsFlow()

    protected suspend fun emit(event: UiEvent) {
        channel.send(event)
    }

    /** Runs [block]; its returned text (or the error) is shown as feedback. */
    protected fun perform(block: suspend () -> String) {
        viewModelScope.launch {
            val message = try {
                block()
            } catch (e: BusinessRuleException) {
                e.message ?: "Action failed."
            } catch (e: Exception) {
                "Unexpected error: ${e.message}"
            }
            emit(UiEvent.Message(message))
        }
    }
}
