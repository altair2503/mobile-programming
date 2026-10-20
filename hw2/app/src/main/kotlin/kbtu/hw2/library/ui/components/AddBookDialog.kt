package kbtu.hw2.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kbtu.hw2.library.domain.Book

data class AddBookFormState(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val genres: String = "",
    val pages: String = "300",
    val isDigital: Boolean = false,
)

@Composable
fun AddBookDialog(
    form: AddBookFormState,
    onFormChange: (AddBookFormState) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (Book) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add book") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = form.id,
                    onValueChange = { onFormChange(form.copy(id = it)) },
                    label = { Text("Id") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { onFormChange(form.copy(title = it)) },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = form.author,
                    onValueChange = { onFormChange(form.copy(author = it)) },
                    label = { Text("Author") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = form.genres,
                    onValueChange = { onFormChange(form.copy(genres = it)) },
                    label = { Text("Genres (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (!form.isDigital) {
                    OutlinedTextField(
                        value = form.pages,
                        onValueChange = { onFormChange(form.copy(pages = it)) },
                        label = { Text("Pages") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                RowWithCheckbox(
                    checked = form.isDigital,
                    onCheckedChange = { onFormChange(form.copy(isDigital = it)) },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val genres = form.genres
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .toSet()
                    val pages = form.pages.toIntOrNull() ?: 0
                    if (form.id.isNotBlank() && form.title.isNotBlank() && form.author.isNotBlank()) {
                        onConfirm(
                            Book(
                                id = form.id.trim(),
                                title = form.title.trim(),
                                author = form.author.trim(),
                                genres = genres,
                                pages = if (form.isDigital) 0 else pages,
                                available = true,
                                isDigital = form.isDigital,
                            ),
                        )
                    }
                },
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun RowWithCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text("Digital (e-book)")
    }
}
