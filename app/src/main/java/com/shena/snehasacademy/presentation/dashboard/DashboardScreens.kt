package com.shena.snehasacademy.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.AcademyLogo
import com.shena.snehasacademy.core.components.AppToolbar
import com.shena.snehasacademy.core.components.DashboardCard
import com.shena.snehasacademy.core.components.ProfileCard
import com.shena.snehasacademy.core.components.SectionHeader
import com.shena.snehasacademy.core.navigation.Route
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel? = null,
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit
) {
    val vm = viewModel ?: AdminDashboardViewModel()
    Scaffold(topBar = { AdminHeaderBar() }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }
            item {
                AdminStatsGrid(
                    totalStudents = vm.totalStudents,
                    totalCourses = vm.totalCourses,
                    totalEnrollments = vm.totalEnrollments,
                    totalCertificates = vm.totalCertificates
                )
            }
            item {
                SectionHeader(
                    "Management",
                    Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            items(vm.menu) { (route, title) ->
                DashboardCard(
                    title,
                    adminSubtitle(title),
                    onClick = { if (route != Route.AdminDashboard) onNavigate(route) },
                    icon = adminMenuIcon(title)
                )
            }
            item {
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AdminHeaderBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF7E8))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        AdminHeaderCard()
    }
}

@Composable
private fun AdminHeaderCard() {
    val greeting = remember { timeBasedGreeting() }
    val dateText = remember { SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E8))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AcademyLogo(Modifier.size(90.dp))
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "$greeting, Admin 👋",
                    modifier = Modifier.fillMaxWidth(),
                    color = AcademyGreen,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "Sneha's Mehendi & Art Academy",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF4B260C),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                    softWrap = true
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("📅", style = MaterialTheme.typography.labelSmall)
                    Text(dateText, style = MaterialTheme.typography.labelSmall, color = Color(0xFF7A4A24))
                }
            }
        }
    }
}

private fun timeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private val StudentsBg = Color(0xFFDCF3E1)
private val StudentsAccent = Color(0xFF2E8B57)
private val CoursesBg = Color(0xFFEAE4FA)
private val CoursesAccent = Color(0xFF6C4FC1)
private val EnrollmentsBg = Color(0xFFFCEBD9)
private val EnrollmentsAccent = Color(0xFFD97706)
private val CertificatesBg = Color(0xFFDBF3EE)
private val CertificatesAccent = Color(0xFF0F9D82)

@Composable
private fun AdminStatsGrid(
    totalStudents: Int,
    totalCourses: Int,
    totalEnrollments: Int,
    totalCertificates: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            DashboardStatCard("🎓", StudentsBg, StudentsAccent, "Students", totalStudents.toString(), "Total Students", Modifier.weight(1f))
            DashboardStatCard("📘", CoursesBg, CoursesAccent, "Courses", totalCourses.toString(), "Total Courses", Modifier.weight(1f))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
        ) {
            DashboardStatCard("📋", EnrollmentsBg, EnrollmentsAccent, "Enrollments", totalEnrollments.toString(), "Total Enrollments", Modifier.weight(1f))
            DashboardStatCard("🏅", CertificatesBg, CertificatesAccent, "Certificates", totalCertificates.toString(), "Total Certificates", Modifier.weight(1f))
        }
    }
}

@Composable
private fun DashboardStatCard(
    icon: String,
    iconBg: Color,
    accent: Color,
    label: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = iconBg.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, style = MaterialTheme.typography.bodyMedium)
                }
                Text(label, color = accent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            }
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel? = null,
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit
) {
    val vm = viewModel ?: StudentDashboardViewModel()
    Scaffold(topBar = { AppToolbar("Student Dashboard") }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(Modifier.padding(top = 12.dp)) {
                    Text("Namaste, Artist", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Track your learning journey beautifully.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item { ProfileCard("Aarohi Sharma", "Bridal Mehendi Masterclass") }
            items(vm.menu) { (route, title) ->
                DashboardCard(title, studentSubtitle(title), onClick = { if (route != Route.StudentDashboard) onNavigate(route) })
            }
            item {
                OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun adminSubtitle(title: String) = when (title) {
    "Students" -> "Add, view, and manage academy students."
    "Courses" -> "Organize art and mehendi courses."
    "Enrollments" -> "Prepare enrollment workflows."
    "Attendance" -> "Track class presence and history."
    "Fee Management" -> "Monitor dues and collections."
    "Certificates" -> "Plan certificates for completed courses."
    else -> "Academy overview and quick actions."
}

private fun adminMenuIcon(title: String): ImageVector = when (title) {
    "Students" -> Icons.Rounded.Groups
    "Courses" -> Icons.AutoMirrored.Rounded.MenuBook
    "Enrollments" -> Icons.AutoMirrored.Rounded.Assignment
    "Attendance" -> Icons.AutoMirrored.Rounded.FactCheck
    "Fee Management" -> Icons.Rounded.Payments
    "Certificates" -> Icons.Rounded.WorkspacePremium
    else -> Icons.Rounded.Groups
}

private fun studentSubtitle(title: String) = when (title) {
    "My Profile" -> "View your student information."
    "My Courses" -> "See enrolled classes and progress."
    "Attendance" -> "Check your attendance record."
    "Fee Status" -> "Review payment status."
    "Certificates" -> "View earned certificates."
    else -> "Your learning home."
}

@Preview(showBackground = true)
@Composable
private fun AdminDashboardPreview() {
    SnehasAcademyTheme { AdminDashboardScreen(onNavigate = {}, onLogout = {}) }
}

@Preview(showBackground = true)
@Composable
private fun StudentDashboardPreview() {
    SnehasAcademyTheme { StudentDashboardScreen(onNavigate = {}, onLogout = {}) }
}
