package com.shena.snehasacademy.presentation.certificates

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.core.components.CertificateViewDialog
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.HeaderIconBadge
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.AcademyGold
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
    val courseDuration: String,
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
            courseDuration = course?.duration.orEmpty(),
            issueDate = certificate.issueDate,
            createdBy = certificate.createdBy
        )
    }

private val CertificateAvatarPalette = listOf(
    Color(0xFF2E8B57) to Color(0xFFDCF3E1),
    Color(0xFF2E6FD9) to Color(0xFFDCEAFB),
    Color(0xFF6C4FC1) to Color(0xFFEAE4FA),
    Color(0xFFD97706) to Color(0xFFFCEBD9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateScreen(viewModel: CertificateViewModel? = null, onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedRow by remember { mutableStateOf<CertificateRow?>(null) }
    var previewRow by remember { mutableStateOf<CertificateRow?>(null) }

    previewRow?.let { row ->
        BackHandler { previewRow = null }
        CertificateFullScreenScreen(
            data = CertificateTemplateData(
                certificateId = row.certificateId,
                studentId = row.studentId,
                studentName = row.studentName,
                courseName = row.courseName,
                courseDuration = row.courseDuration,
                completionDate = row.issueDate,
                issueDate = row.issueDate
            ),
            onBack = { previewRow = null }
        )
        return
    }

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

    var isPullRefreshing by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel?.isLoading) {
        if (viewModel?.isLoading == false) isPullRefreshing = false
    }

    Scaffold(
        topBar = {
            SubtitledHeader(title = "Certificates", subtitle = "View all issued certificates", onBack = onBack) {
                HeaderIconBadge(icon = Icons.Rounded.WorkspacePremium, tint = AcademyGold, background = Color(0xFFFCEBD9))
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                viewModel?.refresh()
            },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
            ) {
                item { SearchBar(query, { query = it }, placeholder = "Search by certificate ID, name, or student ID...") }
                if (viewModel?.isLoading == true && rows.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item {
                        EmptyState(
                            "No certificates found",
                            "Generated certificates will appear here.",
                            icon = {
                                Icon(
                                    Icons.Rounded.WorkspacePremium,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        )
                    }
                } else {
                    items(filtered) { row ->
                        CertificateRowCard(
                            row,
                            Modifier.padding(top = 2.dp),
                            onClick = { selectedRow = row }
                        )
                    }
                    item {
                        Text(
                            "Showing ${filtered.size} of ${rows.size} certificates",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            onDismiss = { selectedRow = null },
            onViewPreview = {
                selectedRow = null
                previewRow = row
            }
        )
    }
}

@Composable
private fun CertificateRowCard(row: CertificateRow, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val (accent, bg) = remember(row.studentId) {
        CertificateAvatarPalette[(row.studentId.hashCode() and Int.MAX_VALUE) % CertificateAvatarPalette.size]
    }
    val initials = remember(row.studentName) {
        row.studentName.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }

    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, color = accent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(row.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "Student ID: ${row.studentId}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Course: ${row.courseName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Certificate ID: ${row.certificateId}",
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
                        "Issue Date: ${row.issueDate}",
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
private fun CertificatePreview() {
    SnehasAcademyTheme { CertificateScreen(onBack = {}) }
}
