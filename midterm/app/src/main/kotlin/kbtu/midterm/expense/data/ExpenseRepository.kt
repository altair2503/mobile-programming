package kbtu.midterm.expense.data

import kbtu.midterm.expense.domain.Expense

class ExpenseRepository {
    private val items = mutableListOf<Expense>()

    fun getAll(): List<Expense> = items.sortedByDescending { it.timestamp }

    fun findById(id: String): Expense? = items.find { it.id == id }

    fun add(expense: Expense) {
        items.add(expense)
    }

    fun update(expense: Expense) {
        val index = items.indexOfFirst { it.id == expense.id }
        if (index >= 0) {
            items[index] = expense
        }
    }

    fun delete(id: String): Boolean {
        val expense = findById(id) ?: return false
        return items.remove(expense)
    }

    fun seed(samples: List<Expense>) {
        items.clear()
        items.addAll(samples)
    }
}
