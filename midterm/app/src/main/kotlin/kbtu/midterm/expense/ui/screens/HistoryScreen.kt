package kbtu.midterm.expense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.domain.ExpenseCategory
import kbtu.midterm.expense.ui.ExpenseViewModel
import kbtu.midterm.expense.ui.components.CategoryFilterRow
import kbtu.midterm.expense.ui.components.EmptyState
import kbtu.midterm.expense.ui.components.ExpenseCard
import kbtu.midterm.expense.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ExpenseViewModel,
    expenses: List<Expense>,
    onExpenseClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<ExpenseCategory?>(null) }

    val filtered by remember(expenses, searchQuery, selectedCategory) {
        derivedStateOf {
            expenses.filter { expense ->
                val matchesSearch = searchQuery.isBlank() ||
                    expense.note.contains(searchQuery, ignoreCase = true) ||
                    expense.category.label.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || expense.category == selectedCategory
                matchesSearch && matchesCategory
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("History") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.screenPadding),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.screenPadding),
                label = { Text("Search by note or category") },
                singleLine = true,
            )
            CategoryFilterRow(
                selected = selectedCategory,
                onSelected = { selectedCategory = it },
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
            ) {
                if (filtered.isEmpty()) {
                    item { EmptyState("No expenses match your filters.") }
                } else {
                    items(filtered, key = { it.id }) { expense ->
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
}
