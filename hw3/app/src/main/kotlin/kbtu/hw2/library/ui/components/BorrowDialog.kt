package kbtu.hw2.library.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kbtu.hw2.library.domain.LoanPolicy

private const val MAX_BORROWER_NAME = 60

@Composable
fun BorrowDialog(
    bookTitle: String,
    onConfirm: (borrowerName: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var showError by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrow \"$bookTitle\"") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    showError = false
                },
                label = { Text("Borrower name") },
                singleLine = true,
                isError = showError,
                supportingText = {
                    Text(
                        if (showError) {
                            "Name is required (max $MAX_BORROWER_NAME characters)"
                        } else {
                            "Loan period: ${LoanPolicy.LOAN_PERIOD_DAYS} days"
                        },
                    )
                },
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isEmpty() || trimmed.length > MAX_BORROWER_NAME) {
                        showError = true
                    } else {
                        onConfirm(trimmed)
                    }
                },
            ) { Text("Borrow") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
