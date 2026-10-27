package kbtu.midterm.expense.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kbtu.midterm.expense.data.ExpenseRepository
import kbtu.midterm.expense.domain.CategorySummary
import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.domain.ExpenseCategory
import kbtu.midterm.expense.network.AiExpenseParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(
    private val repository: ExpenseRepository = ExpenseRepository(),
    private val aiParser: AiExpenseParser = AiExpenseParser(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        seedSampleData()
        refreshFromRepository()
    }

    fun addExpense(
        amount: Double,
        category: ExpenseCategory,
        note: String,
    ) {
        if (amount <= 0) {
            showMessage("Amount must be greater than zero.")
            return
        }
        repository.add(
            Expense(
                amount = amount,
                category = category,
                note = note.trim(),
            ),
        )
        refreshFromRepository()
        showMessage("Expense added.")
    }

    fun updateExpense(
        id: String,
        amount: Double,
        category: ExpenseCategory,
        note: String,
    ) {
        val existing = repository.findById(id)
        if (existing == null) {
            showMessage("Expense not found.")
            return
        }
        if (amount <= 0) {
            showMessage("Amount must be greater than zero.")
            return
        }
        repository.update(
            existing.copy(
                amount = amount,
                category = category,
                note = note.trim(),
            ),
        )
        refreshFromRepository()
        showMessage("Expense updated.")
    }

    fun deleteExpense(id: String) {
        if (repository.delete(id)) {
            refreshFromRepository()
            showMessage("Expense deleted.")
        } else {
            showMessage("Could not delete expense.")
        }
    }

    fun parseWithAi(text: String) {
        if (_uiState.value.isAiParsing) return
        _uiState.update { it.copy(isAiParsing = true, aiDraft = null) }
        viewModelScope.launch {
            aiParser.parse(text)
                .onSuccess { draft ->
                    _uiState.update { it.copy(aiDraft = draft) }
                    showMessage("AI parsed your expense. Review and save.")
                }
                .onFailure { error ->
                    showMessage(error.message ?: "AI parsing failed.")
                }
            _uiState.update { it.copy(isAiParsing = false) }
        }
    }

    fun clearAiDraft() {
        _uiState.update { it.copy(aiDraft = null) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun recentExpenses(limit: Int = 5): List<Expense> =
        _uiState.value.expenses.take(limit)

    private fun seedSampleData() {
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L
        repository.seed(
            listOf(
                Expense(
                    id = "seed-1",
                    amount = 4500.0,
                    category = ExpenseCategory.Food,
                    note = "Lunch at cafeteria",
                    timestamp = now - dayMs,
                ),
                Expense(
                    id = "seed-2",
                    amount = 1200.0,
                    category = ExpenseCategory.Transport,
                    note = "Metro top-up",
                    timestamp = now - 2 * dayMs,
                ),
                Expense(
                    id = "seed-3",
                    amount = 8900.0,
                    category = ExpenseCategory.Shopping,
                    note = "Groceries",
                    timestamp = now - 3 * dayMs,
                ),
            ),
        )
    }

    private fun refreshFromRepository() {
        val expenses = repository.getAll()
        val monthTotal = totalThisMonth(expenses)
        val summaries = byCategory(expenses)
        _uiState.update {
            it.copy(
                expenses = expenses,
                monthTotal = monthTotal,
                categorySummaries = summaries,
            )
        }
    }

    private fun totalThisMonth(expenses: List<Expense>): Double {
        val cal = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        return expenses
            .filter { expense ->
                cal.timeInMillis = expense.timestamp
                cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year
            }
            .sumOf { it.amount }
    }

    private fun byCategory(expenses: List<Expense>): List<CategorySummary> =
        expenses
            .groupBy { it.category }
            .map { (category, list) ->
                CategorySummary(
                    category = category,
                    total = list.sumOf { it.amount },
                    count = list.size,
                )
            }
            .sortedByDescending { it.total }

    private fun showMessage(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }
}
