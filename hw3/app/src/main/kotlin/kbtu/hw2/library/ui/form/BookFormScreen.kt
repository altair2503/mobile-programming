package kbtu.hw2.library.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kbtu.hw2.library.ui.common.CollectUiEvents
import kbtu.hw2.library.ui.components.BookCover
import kbtu.hw2.library.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookFormScreen(
    viewModel: BookFormViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    CollectUiEvents(viewModel.events, onNavigateBack = onBack)
    val errors = form.errors

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (form.isEditMode) "Edit book" else "New book") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormTextField(
                value = form.id,
                onValueChange = viewModel::onIdChange,
                label = "Id",
                hint = if (form.isEditMode) "Id cannot be changed" else "Unique, 1–20 chars: letters, digits, - or _",
                error = errors.id,
                enabled = !form.isEditMode,
            )
            FormTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChange,
                label = "Title",
                hint = "Up to ${BookFormValidator.TITLE_MAX} characters",
                error = errors.title,
            )
            FormTextField(
                value = form.author,
                onValueChange = viewModel::onAuthorChange,
                label = "Author",
                hint = "Up to ${BookFormValidator.AUTHOR_MAX} characters",
                error = errors.author,
            )
            FormTextField(
                value = form.genres,
                onValueChange = viewModel::onGenresChange,
                label = "Genres",
                hint = "Comma-separated, e.g. Fiction, Fantasy",
                error = errors.genres,
            )
            FormTextField(
                value = form.coverUrl,
                onValueChange = viewModel::onCoverUrlChange,
                label = "Cover image URL (optional)",
                hint = "Link to an image, starts with https://",
                error = errors.coverUrl,
                keyboardType = KeyboardType.Uri,
            )
            BookCover(
                coverUrl = form.coverUrl.takeIf { errors.coverUrl == null },
                title = form.title.ifBlank { "Preview" },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(96.dp)
                    .height(144.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = form.isDigital,
                    onCheckedChange = viewModel::onDigitalChange,
                )
                Text("Digital (e-book)")
            }
            if (!form.isDigital) {
                FormTextField(
                    value = form.pages,
                    onValueChange = viewModel::onPagesChange,
                    label = "Pages",
                    hint = "Whole number, 1–${BookFormValidator.PAGES_MAX}",
                    error = errors.pages,
                    keyboardType = KeyboardType.Number,
                )
            }

            Button(
                onClick = viewModel::save,
                enabled = !form.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (form.isEditMode) "Save changes" else "Add book")
            }
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String,
    error: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = { Text(error ?: hint) },
        isError = error != null,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
    )
}
