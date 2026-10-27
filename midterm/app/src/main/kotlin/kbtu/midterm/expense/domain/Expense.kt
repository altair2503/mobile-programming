package kbtu.midterm.expense.domain

import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val currency: String = "KZT",
    val category: ExpenseCategory,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
)

data class CategorySummary(
    val category: ExpenseCategory,
    val total: Double,
    val count: Int,
)
