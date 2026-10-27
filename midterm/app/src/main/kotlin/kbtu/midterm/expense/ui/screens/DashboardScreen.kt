package kbtu.midterm.expense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.ui.ExpenseViewModel
import kbtu.midterm.expense.ui.components.EmptyState
import kbtu.midterm.expense.ui.components.ExpenseCard
import kbtu.midterm.expense.ui.components.SummaryCard
import kbtu.midterm.expense.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    monthTotal: Double,
    recentExpenses: List<Expense>,
    onAddClick: () -> Unit,
    onExpenseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
        ) {
            item {
                SummaryCard(
                    title = "This month",
                    amount = monthTotal,
                    subtitle = "Total spending in the current calendar month",
                )
            }
            item {
                Text(
                    text = "Recent expenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (recentExpenses.isEmpty()) {
                item { EmptyState("No expenses yet. Tap + to add one.") }
            } else {
                items(recentExpenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onClick = { onExpenseClick(expense.id) },
                        onDelete = { viewModel.deleteExpense(expense.id) },
                    )
                }
            }
        }
    }
}
