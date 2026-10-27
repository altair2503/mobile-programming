package kbtu.midterm.expense.ui

import kbtu.midterm.expense.domain.CategorySummary
import kbtu.midterm.expense.domain.Expense

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val userMessage: String? = null,
    val aiInsights: String? = null,
    val isAiInsightsLoading: Boolean = false,
    val monthTotal: Double = 0.0,
    val categorySummaries: List<CategorySummary> = emptyList(),
)
