package kbtu.midterm.expense.network

import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.domain.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class AiInsightsAnalystTest {
    private val day = 24 * 60 * 60 * 1000L
    private val base = 1_700_000_000_000L
    private fun date(ts: Long) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(ts)

    @Test
    fun emptyListGivesEmptyText() {
        assertEquals("", AiInsightsAnalyst.buildExpensesText(emptyList()))
    }

    @Test
    fun newestFirstWithHeaderAndLineFormat() {
        val old = Expense(id = "a", amount = 100.0, category = ExpenseCategory.Food, note = "lunch", timestamp = base)
        val new = Expense(id = "b", amount = 200.0, category = ExpenseCategory.Bills, note = "rent", timestamp = base + day)
        val lines = AiInsightsAnalyst.buildExpensesText(listOf(old, new)).lines()
        assertEquals("Currency: KZT, 2 expenses, ${date(base)} to ${date(base + day)}", lines[0])
        assertEquals("b | ${date(base + day)} | 200.0 KZT | Bills | rent", lines[1])
        assertEquals("a | ${date(base)} | 100.0 KZT | Food | lunch", lines[2])
    }
}
