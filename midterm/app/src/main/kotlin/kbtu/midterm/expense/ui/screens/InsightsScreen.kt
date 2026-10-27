package kbtu.midterm.expense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kbtu.midterm.expense.domain.CategorySummary
import kbtu.midterm.expense.ui.components.EmptyState
import kbtu.midterm.expense.ui.theme.Dimens
import kbtu.midterm.expense.ui.util.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    summaries: List<CategorySummary>,
    monthTotal: Double,
    modifier: Modifier = Modifier,
) {
    val maxTotal = summaries.maxOfOrNull { it.total } ?: 1.0

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Insights") }) },
    ) { innerPadding ->
        if (summaries.isEmpty()) {
            EmptyState(
                message = "Add expenses to see category breakdown.",
                modifier = Modifier.padding(innerPadding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(Dimens.screenPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.cardSpacing),
            ) {
                item {
                    Text(
                        text = "Spending by category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Month total: ${formatAmount(monthTotal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                items(summaries, key = { it.category.name }) { summary ->
                    CategoryBreakdownCard(summary = summary, maxTotal = maxTotal)
                }
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(
    summary: CategorySummary,
    maxTotal: Double,
) {
    val progress = (summary.total / maxTotal).toFloat().coerceIn(0f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = summary.category.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${formatAmount(summary.total)} · ${summary.count} expense(s)",
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
