package kbtu.midterm.expense.ui

import kbtu.midterm.expense.domain.CategorySummary
import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.domain.ExpenseDraft

data class ExpenseUiState(
    val expenses: List<Expense> = emptyList(),
    val isAiParsing: Boolean = false,
    val userMessage: String? = null,
    val aiDraft: ExpenseDraft? = null,
    val monthTotal: Double = 0.0,
    val categorySummaries: List<CategorySummary> = emptyList(),
)
