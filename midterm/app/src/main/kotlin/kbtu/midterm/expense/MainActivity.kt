package kbtu.midterm.expense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import kbtu.midterm.expense.ui.ExpenseViewModel
import kbtu.midterm.expense.ui.navigation.ExpenseNavHost
import kbtu.midterm.expense.ui.theme.ExpenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTheme {
                val expenseViewModel: ExpenseViewModel = viewModel()
                ExpenseNavHost(viewModel = expenseViewModel)
            }
        }
    }
}
