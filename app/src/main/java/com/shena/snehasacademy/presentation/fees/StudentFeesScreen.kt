package com.shena.snehasacademy.presentation.fees

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
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

private val StudentFeesBg = Color(0xFFFCEBD9)
private val StudentFeesAccent = Color(0xFFD97706)

@Composable
fun StudentFeesScreen(
    studentId: String,
    viewModel: StudentDashboardViewModel? = null,
    onOpenHistory: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    LaunchedEffect(studentId, viewModel) {
        viewModel?.loadStudent(studentId)
    }

    val student = viewModel?.student ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val isLoading = viewModel != null && viewModel.isLoading && viewModel.student == null

    Scaffold(
        topBar = { SubtitledHeader(title = "Fee Status", subtitle = "Your fees across all enrolled courses", onBack = onBack) }
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
                    StudentFeeCard(enrollment, onClick = { onOpenHistory(studentId, enrollment.id) })
                }
            }
        }
    }
}

@Composable
private fun StudentFeeCard(enrollment: CourseEnrollment, onClick: () -> Unit) {
    val due = (enrollment.fees - enrollment.amountPaid).coerceAtLeast(0)
    val paymentStatus = if (due <= 0) "Paid" else "Due"
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = StudentFeesBg.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(StudentFeesBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Payments, contentDescription = null, tint = StudentFeesAccent, modifier = Modifier.size(18.dp))
            }
            Column(Modifier.weight(1f).padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(enrollment.courseName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Fees ₹${enrollment.fees}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Paid ₹${enrollment.amountPaid}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Due ₹$due",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusBadge(paymentStatus)
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp).padding(start = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentFeesPreview() {
    SnehasAcademyTheme { StudentFeesScreen(studentId = "SMAA-STD-001", onBack = {}) }
}
