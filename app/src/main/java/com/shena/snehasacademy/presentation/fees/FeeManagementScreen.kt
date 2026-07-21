package com.shena.snehasacademy.presentation.fees

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.core.components.InfoCard
import com.shena.snehasacademy.core.components.LabeledInfo
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SectionHeader
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.presentation.enrollments.EnrollmentViewModel

private data class FeeRow(
    val studentId: String,
    val enrollmentId: String,
    val studentName: String,
    val courseName: String,
    val fees: Int,
    val amountPaid: Int
) {
    val due: Int get() = (fees - amountPaid).coerceAtLeast(0)
}

private fun feeRowsFrom(students: List<Student>): List<FeeRow> =
    students.flatMap { student ->
        student.enrollments.map { enrollment ->
            FeeRow(student.id, enrollment.id, student.name, enrollment.courseName, enrollment.fees, enrollment.amountPaid)
        }
    }

@Composable
fun FeeManagementScreen(
    viewModel: EnrollmentViewModel? = null,
    onOpenHistory: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    val students = viewModel?.students ?: MockData.students
    val rows = remember(students) { feeRowsFrom(students) }
    val totalCollected = remember(rows) { rows.sumOf { it.amountPaid } }
    val totalDue = remember(rows) { rows.sumOf { it.due } }

    LifecycleResumeEffect(viewModel) {
        viewModel?.refresh()
        onPauseOrDispose { }
    }

    ScreenScaffold("Fee Management", true, onBack) { contentModifier ->
        LazyColumn(
            modifier = contentModifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    InfoCard("Collected", "₹$totalCollected", Modifier.weight(1f))
                    InfoCard("Pending", "₹$totalDue", Modifier.weight(1f))
                }
            }
            item { SectionHeader("Payments by Enrollment", Modifier.padding(top = 4.dp)) }
            if (viewModel?.isLoading == true && rows.isEmpty()) {
                item { LoadingView() }
            }
            items(rows) { row ->
                FeePaymentRowCard(row, onClick = { onOpenHistory(row.studentId, row.enrollmentId) })
            }
        }
    }
}

@Composable
private fun FeePaymentRowCard(row: FeeRow, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(row.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(row.courseName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (row.due <= 0) "Paid" else "Due")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                LabeledInfo("Fees", "₹${row.fees}", Modifier.weight(1f))
                LabeledInfo("Paid", "₹${row.amountPaid}", Modifier.weight(1f))
                LabeledInfo("Due", "₹${row.due}", Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeeManagementPreview() {
    SnehasAcademyTheme { FeeManagementScreen {} }
}
