package kbtu.midterm.expense.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val amountFormat = NumberFormat.getNumberInstance(Locale("en", "KZ"))
private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

fun formatAmount(amount: Double, currency: String = "KZT"): String =
    "${amountFormat.format(amount)} $currency"

fun formatDate(timestamp: Long): String =
    dateFormat.format(Date(timestamp))
