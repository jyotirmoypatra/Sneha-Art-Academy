package com.shena.snehasacademy.presentation.students

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.CertificateViewDialog
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.HeaderIconBadge
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.AcademyGold
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.presentation.dashboard.StudentDashboardViewModel

private data class StudentCertificateRow(
    val certificate: Certificate,
    val enrollment: CourseEnrollment
)

@Composable
fun StudentCertificatesScreen(
    studentId: String,
    viewModel: StudentDashboardViewModel? = null,
    onBack: () -> Unit
) {
    LaunchedEffect(studentId, viewModel) {
        viewModel?.loadStudent(studentId)
    }

    val student = viewModel?.student ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val isLoadingStudent = viewModel != null && viewModel.isLoading && viewModel.student == null

    var rows by remember { mutableStateOf<List<StudentCertificateRow>>(emptyList()) }
    var isLoadingCertificates by remember(studentId) { mutableStateOf(false) }
    var selectedRow by remember { mutableStateOf<StudentCertificateRow?>(null) }

    LaunchedEffect(student.id, viewModel) {
        val certifiedEnrollments = student.enrollments.filter { it.certificateId.isNotBlank() }
        if (viewModel != null && certifiedEnrollments.isNotEmpty()) {
            isLoadingCertificates = true
            rows = certifiedEnrollments.mapNotNull { enrollment ->
                viewModel.getCertificate(enrollment.id)?.let { certificate -> StudentCertificateRow(certificate, enrollment) }
            }
            isLoadingCertificates = false
        } else {
            rows = emptyList()
        }
    }

    Scaffold(
        topBar = {
            SubtitledHeader(title = "My Certificates", subtitle = "Certificates issued to you", onBack = onBack) {
                HeaderIconBadge(icon = Icons.Rounded.WorkspacePremium, tint = AcademyGold, background = Color(0xFFFCEBD9))
            }
        }
    ) { padding ->
        if (isLoadingStudent || isLoadingCertificates) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { LoadingView() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (rows.isEmpty()) {
                EmptyState(
                    "No certificates yet",
                    "Certificates issued for your completed courses will appear here.",
                    icon = {
                        Icon(
                            Icons.Rounded.WorkspacePremium,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                )
            } else {
                rows.forEach { row ->
                    StudentCertificateCard(row, onClick = { selectedRow = row })
                }
            }
        }
    }

    selectedRow?.let { row ->
        CertificateViewDialog(
            certificateId = row.certificate.id,
            studentName = student.name,
            studentId = student.id,
            courseName = row.enrollment.courseName,
            issueDate = row.certificate.issueDate,
            createdBy = row.certificate.createdBy,
            onDismiss = { selectedRow = null }
        )
    }
}

@Composable
private fun StudentCertificateCard(row: StudentCertificateRow, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFFCEBD9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = AcademyGold, modifier = Modifier.size(20.dp))
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(row.enrollment.courseName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Certificate ID: ${row.certificate.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        "Issue Date: ${row.certificate.issueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp).size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentCertificatesPreview() {
    SnehasAcademyTheme { StudentCertificatesScreen(studentId = "SMAA-STD-001", onBack = {}) }
}
