package com.shena.snehasacademy.presentation.students

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.presentation.dashboard.StudentDashboardViewModel

private val CoursesSectionBg = Color(0xFFEAE4FA)
private val CoursesSectionAccent = Color(0xFF6C4FC1)

@Composable
fun StudentCoursesScreen(
    studentId: String,
    viewModel: StudentDashboardViewModel? = null,
    onBack: () -> Unit
) {
    LaunchedEffect(studentId, viewModel) {
        viewModel?.loadStudent(studentId)
    }

    val student = viewModel?.student ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val isLoading = viewModel != null && viewModel.isLoading && viewModel.student == null

    Scaffold(
        topBar = { SubtitledHeader(title = "My Courses", subtitle = "Courses you're enrolled in", onBack = onBack) }
    ) { padding ->
        if (isLoading) {
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
            if (student.enrollments.isEmpty()) {
                Text(
                    "No course enrollments yet.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                student.enrollments.forEach { enrollment ->
                    StudentCourseCard(enrollment)
                }
            }
        }
    }
}

@Composable
private fun StudentCourseCard(enrollment: CourseEnrollment) {
    val paymentStatus = if (enrollment.fees > 0 && enrollment.amountPaid >= enrollment.fees) "Paid" else "Due"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CoursesSectionBg.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CoursesSectionBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Spa, contentDescription = null, tint = CoursesSectionAccent, modifier = Modifier.size(18.dp))
                }
                Text(
                    enrollment.courseName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            StudentCourseMetaRow(Icons.Rounded.CalendarMonth, "Enrollment Date") {
                Text(enrollment.enrollmentDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            StudentCourseMetaRow(Icons.Rounded.Star, "Status") { StatusBadge(enrollment.status) }
            StudentCourseMetaRow(Icons.Rounded.Payments, "Fee Status") { StatusBadge(paymentStatus) }
            StudentCourseMetaRow(Icons.Rounded.WorkspacePremium, "Certificate Status") {
                if (enrollment.certificateId.isNotBlank()) {
                    StatusBadge("Issued")
                } else {
                    Text("Not Generated", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun StudentCourseMetaRow(icon: ImageVector, label: String, value: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        value()
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentCoursesPreview() {
    SnehasAcademyTheme { StudentCoursesScreen(studentId = "SMAA-STD-001") { } }
}
