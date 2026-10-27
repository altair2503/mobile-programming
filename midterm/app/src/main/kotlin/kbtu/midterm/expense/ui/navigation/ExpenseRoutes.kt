package kbtu.midterm.expense.ui.navigation

object ExpenseRoutes {
    const val Dashboard = "dashboard"
    const val History = "history"
    const val Insights = "insights"
    const val Add = "add"
    const val Edit = "edit/{expenseId}"

    fun edit(expenseId: String): String = "edit/$expenseId"
}
