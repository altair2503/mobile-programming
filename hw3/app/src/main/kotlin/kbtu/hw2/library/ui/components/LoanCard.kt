package kbtu.hw2.library.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kbtu.hw2.library.domain.Loan
import kbtu.hw2.library.domain.LoanPolicy
import kbtu.hw2.library.domain.LoanStatus
import kbtu.hw2.library.ui.common.formatDate

@Composable
fun LoanCard(
    loan: Loan,
    now: Long,
    modifier: Modifier = Modifier,
    showBookTitle: Boolean = false,
    onReturn: () -> Unit = {},
    onExtend: () -> Unit = {},
) {
    val status = loan.statusAt(now)
    val statusColor = when (status) {
        LoanStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        LoanStatus.OVERDUE -> MaterialTheme.colorScheme.error
        LoanStatus.RETURNED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusLabel = when (status) {
        LoanStatus.ACTIVE -> "Active"
        LoanStatus.OVERDUE -> "Overdue"
        LoanStatus.RETURNED -> "Returned"
    }
    val dateLine = when (status) {
        LoanStatus.ACTIVE -> "Due ${loan.dueAt.formatDate()}"
        LoanStatus.OVERDUE -> "Was due ${loan.dueAt.formatDate()}"
        LoanStatus.RETURNED -> "Returned ${loan.returnedAt?.formatDate().orEmpty()}"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (showBookTitle) {
                BookCover(
                    coverUrl = loan.bookCoverUrl,
                    title = loan.bookTitle,
                    modifier = Modifier
                        .width(56.dp)
                        .height(80.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (showBookTitle) {
                    Text(
                        text = loan.bookTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "Borrower: ${loan.borrowerName}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                StatusBadge(text = statusLabel, color = statusColor)
                Text(
                    text = dateLine,
                    style = MaterialTheme.typography.labelLarge,
                    color = statusColor,
                )
                Text(
                    text = "Borrowed ${loan.borrowedAt.formatDate()} · renewals ${loan.renewCount}/${LoanPolicy.MAX_RENEWALS}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (status != LoanStatus.RETURNED) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onReturn) { Text("Return") }
                        OutlinedButton(onClick = onExtend, enabled = loan.canExtend) {
                            Text("Extend +${LoanPolicy.LOAN_PERIOD_DAYS}d")
                        }
                    }
                }
            }
        }
    }
}
