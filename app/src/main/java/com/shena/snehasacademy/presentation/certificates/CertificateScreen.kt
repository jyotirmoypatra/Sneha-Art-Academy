package com.shena.snehasacademy.presentation.certificates

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.core.components.CertificateViewDialog
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.Student

private data class CertificateRow(
    val certificateId: String,
    val studentName: String,
    val studentId: String,
    val courseName: String,
    val issueDate: String,
    val createdBy: String
)

private fun buildRows(certificates: List<Certificate>, students: List<Student>, courses: List<Course>): List<CertificateRow> =
    certificates.map { certificate ->
        val student = students.find { it.id == certificate.studentId }
        val course = courses.find { it.id == certificate.courseId }
        CertificateRow(
            certificateId = certificate.id,
            studentName = student?.name.orEmpty().ifBlank { "Unknown Student" },
            studentId = certificate.studentId,
            courseName = course?.title.orEmpty().ifBlank { "Unknown Course" },
            issueDate = certificate.issueDate,
            createdBy = certificate.createdBy
        )
    }

@Composable
fun CertificateScreen(viewModel: CertificateViewModel? = null, onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedRow by remember { mutableStateOf<CertificateRow?>(null) }

    val certificates = viewModel?.certificates ?: emptyList()
    val students = viewModel?.students ?: MockData.students
    val courses = viewModel?.courses ?: MockData.courses
    val rows = remember(certificates, students, courses) { buildRows(certificates, students, courses) }
    val filtered = remember(query, rows) {
        if (query.isBlank()) {
            rows
        } else {
            rows.filter {
                it.certificateId.contains(query, ignoreCase = true) ||
                    it.studentName.contains(query, ignoreCase = true) ||
                    it.studentId.contains(query, ignoreCase = true)
            }
        }
    }

    LifecycleResumeEffect(viewModel) {
        viewModel?.refresh()
        onPauseOrDispose { }
    }

    ScreenScaffold("Certificates", true, onBack) { contentModifier ->
        LazyColumn(
            modifier = contentModifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SearchBar(query, { query = it }, placeholder = "Search by certificate ID, name, or student ID") }
            if (viewModel?.isLoading == true && rows.isEmpty()) {
                item { LoadingView() }
            } else if (filtered.isEmpty()) {
                item { EmptyState("No certificates found", "Generated certificates will appear here.") }
            } else {
                items(filtered) { row ->
                    CertificateRowCard(
                        row,
                        Modifier.padding(top = 2.dp),
                        onClick = { selectedRow = row }
                    )
                }
            }
        }
    }

    selectedRow?.let { row ->
        CertificateViewDialog(
            certificateId = row.certificateId,
            studentName = row.studentName,
            studentId = row.studentId,
            courseName = row.courseName,
            issueDate = row.issueDate,
            createdBy = row.createdBy,
            onDismiss = { selectedRow = null }
        )
    }
}

@Composable
private fun CertificateRowCard(row: CertificateRow, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(row.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CertificateDetailRow("Student ID", row.studentId)
                CertificateDetailRow("Certificate ID", row.certificateId)
                CertificateDetailRow("Issue Date", row.issueDate)
            }
        }
    }
}

@Composable
private fun CertificateDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            "$label:",
            modifier = Modifier.width(108.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CertificatePreview() {
    SnehasAcademyTheme { CertificateScreen(onBack = {}) }
}
