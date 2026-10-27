package kbtu.midterm.expense.network

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.2,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoice> = emptyList(),
)

@Serializable
data class ChatChoice(
    val message: ChatMessage? = null,
)
