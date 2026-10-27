package kbtu.midterm.expense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kbtu.midterm.expense.domain.Expense
import kbtu.midterm.expense.domain.ExpenseCategory
import kbtu.midterm.expense.domain.ExpenseDraft
import kbtu.midterm.expense.ui.ExpenseViewModel
import kbtu.midterm.expense.ui.components.AmountField
import kbtu.midterm.expense.ui.components.CategorySelectRow
import kbtu.midterm.expense.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    expenseId: String?,
    existing: Expense?,
    isAiParsing: Boolean,
    aiDraft: ExpenseDraft?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEdit = expenseId != null
    var amountText by remember(existing?.id) {
        mutableStateOf(existing?.amount?.toString() ?: "")
    }
    var note by remember(existing?.id) {
        mutableStateOf(existing?.note ?: "")
    }
    var category by remember(existing?.id) {
        mutableStateOf(existing?.category ?: ExpenseCategory.Food)
    }
    var aiInput by remember { mutableStateOf("") }

    LaunchedEffect(aiDraft) {
        val draft = aiDraft ?: return@LaunchedEffect
        amountText = draft.amount.toString()
        note = draft.note
        category = draft.category
        viewModel.clearAiDraft()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit expense" else "Add expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
        ) {
            if (!isEdit) {
                OutlinedTextField(
                    value = aiInput,
                    onValueChange = { aiInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Describe expense for AI") },
                    placeholder = { Text("e.g. coffee 1500 at campus") },
                    minLines = 2,
                )
                Button(
                    onClick = { viewModel.parseWithAi(aiInput) },
                    enabled = !isAiParsing && aiInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (isAiParsing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Parse with AI")
                    }
                }
            }

            AmountField(value = amountText, onValueChange = { amountText = it })
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Note") },
                minLines = 2,
            )
            CategorySelectRow(selected = category, onSelected = { category = it })

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (isEdit && expenseId != null) {
                        viewModel.updateExpense(expenseId, amount, category, note)
                    } else {
                        viewModel.addExpense(amount, category, note)
                    }
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (isEdit) "Save changes" else "Save expense")
            }
        }
    }
}
