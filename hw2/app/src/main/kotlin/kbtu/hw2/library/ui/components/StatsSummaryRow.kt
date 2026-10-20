package kbtu.hw2.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kbtu.hw2.library.ui.LibraryStats

@Composable
fun StatsSummaryRow(
    stats: LibraryStats,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AssistChip(
            onClick = {},
            label = { Text("Books: ${stats.bookCount}") },
        )
        AssistChip(
            onClick = {},
            label = { Text("Print pages: ${stats.totalPrintPages}") },
        )
        AssistChip(
            onClick = {},
            label = { Text("Genres: ${stats.genreCount}") },
        )
    }
}
