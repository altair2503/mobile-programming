package kbtu.midterm.expense.network

import kbtu.midterm.expense.domain.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AiInsightsAnalyst(
    private val client: OpenRouterClient = OpenRouterClient(),
) {
    suspend fun analyze(expenses: List<Expense>): Result<String> = withContext(Dispatchers.IO) {
        if (!client.hasApiKey()) {
            return@withContext Result.failure(IllegalStateException("OpenRouter API key is not configured."))
        }
        if (expenses.isEmpty()) {
            return@withContext Result.failure(IllegalArgumentException("Add expenses to get AI insights."))
        }
        val request = ChatCompletionRequest(
            model = "nvidia/nemotron-3-ultra-550b-a55b:free",
            messages = listOf(
                ChatMessage(role = "system", content = SYSTEM_PROMPT),
                ChatMessage(role = "user", content = buildExpensesText(expenses)),
            ),
        )
        client.chatCompletion(request)
    }

    companion object {
        const val SYSTEM_PROMPT =
            "You are an AI Financial Analyst. Analyze all my expenses to identify spending patterns, " +
                "unusual transactions, recurring payments, unnecessary expenses, and changes in spending habits. " +
                "Categorize expenses, compare spending across periods, highlight where I spend the most, " +
                "identify potential savings, and provide practical recommendations to improve my financial health. " +
                "Be concise, data-driven, and proactive. Never invent data, double-count transactions, " +
                "or make assumptions without evidence. Focus on meaningful insights rather than simply " +
                "summarizing transactions."

        fun buildExpensesText(expenses: List<Expense>): String {
            if (expenses.isEmpty()) return ""
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val sorted = expenses.sortedByDescending { it.timestamp }
            val header = "Currency: KZT, ${sorted.size} expenses, " +
                "${fmt.format(Date(sorted.last().timestamp))} to ${fmt.format(Date(sorted.first().timestamp))}"
            val lines = sorted.map {
                "${it.id} | ${fmt.format(Date(it.timestamp))} | ${it.amount} KZT | ${it.category.label} | ${it.note}"
            }
            return (listOf(header) + lines).joinToString("\n")
        }
    }
}
