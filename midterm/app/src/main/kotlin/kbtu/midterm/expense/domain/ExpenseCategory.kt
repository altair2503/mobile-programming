package kbtu.midterm.expense.domain

enum class ExpenseCategory(val label: String) {
    Food("Food"),
    Transport("Transport"),
    Shopping("Shopping"),
    Bills("Bills"),
    Entertainment("Entertainment"),
    Other("Other"),
    ;

    companion object {
        fun fromLabel(value: String): ExpenseCategory? =
            entries.firstOrNull { it.label.equals(value, ignoreCase = true) }
    }
}
