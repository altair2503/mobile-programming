package kbtu.midterm.expense.network

import kbtu.midterm.expense.domain.ExpenseCategory
import kbtu.midterm.expense.domain.ExpenseDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class AiExpenseParser(
    private val client: OpenRouterClient = OpenRouterClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    suspend fun parse(userText: String): Result<ExpenseDraft> = withContext(Dispatchers.IO) {
        if (!client.hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("OpenRouter API key is not configured."))
        }
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Enter a description to parse."))
        }
        val prompt = """
            Extract one expense from the user message. Reply with JSON only, no markdown:
            {"amount": number, "category": "Food|Transport|Shopping|Bills|Entertainment|Other", "note": "string"}
            User message: $trimmed
        """.trimIndent()
        val request = ChatCompletionRequest(
            model = "google/gemma-2-9b-it:free",
            messages = listOf(
                ChatMessage(role = "system", content = "You are a expense parsing assistant. Output valid JSON only."),
                ChatMessage(role = "user", content = prompt),
            ),
        )
        client.chatCompletion(request).mapCatching { content ->
            val cleaned = content.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val parsed = json.decodeFromString<ParsedExpenseJson>(cleaned)
            val category = ExpenseCategory.fromLabel(parsed.category) ?: ExpenseCategory.Other
            if (parsed.amount <= 0) {
                throw IllegalArgumentException("Parsed amount must be positive.")
            }
            ExpenseDraft(
                amount = parsed.amount,
                category = category,
                note = parsed.note.ifBlank { trimmed },
            )
        }
    }
}
