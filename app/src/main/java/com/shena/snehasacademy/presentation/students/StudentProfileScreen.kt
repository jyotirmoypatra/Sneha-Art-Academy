package com.shena.snehasacademy.presentation.students

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shena.snehasacademy.core.components.DetailInfoRow
import com.shena.snehasacademy.core.components.DetailSectionContainer
import com.shena.snehasacademy.core.components.DetailSectionHeaderRow
import com.shena.snehasacademy.core.components.CheckBadgedAvatar
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ProfileMetaItem
import com.shena.snehasacademy.core.components.ProfileSummaryCard
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.presentation.dashboard.StudentDashboardViewModel
import androidx.compose.material3.Text

private val ProfileSectionBg = Color(0xFFEAE4FA)
private val ProfileSectionAccent = Color(0xFF6C4FC1)

@Composable
fun StudentProfileScreen(
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
        topBar = { SubtitledHeader(title = "My Profile", subtitle = "View your registered details", onBack = onBack) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StudentProfileSummaryCard(student)

            DetailSectionContainer {
                DetailSectionHeaderRow(Icons.Rounded.Person, ProfileSectionBg, ProfileSectionAccent, "Personal Information")
                DetailInfoRow(Icons.Rounded.Person, "Full Name", student.name, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.Groups, "Father's / Guardian's Name", student.guardianName, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.CalendarMonth, "Date of Birth", student.dateOfBirth, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.Wc, "Gender", student.gender, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.Call, "Mobile Number", student.mobile, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.LocationOn, "Address", student.address, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent)
                DetailInfoRow(Icons.Rounded.Badge, "Aadhaar Number", student.aadhaarNumber, iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent, showDivider = false)
            }
        }
    }
}

@Composable
private fun StudentProfileSummaryCard(student: Student) {
    val initials = remember(student.name) {
        student.name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }
    ProfileSummaryCard(
        avatar = {
            CheckBadgedAvatar(
                initialsOrIcon = {
                    Text(initials, color = AcademyGreen, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                },
                badgeColor = AcademyGreen
            )
        },
        title = student.name,
        tagText = "Student",
        tagColor = AcademyGreen
    ) {
        ProfileMetaItem(
            Icons.Rounded.Badge, "Student ID", student.id, Modifier.weight(1f),
            iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent
        )
        ProfileMetaItem(
            Icons.Rounded.Call, "Mobile", student.mobile, Modifier.weight(1f),
            iconBg = ProfileSectionBg, iconTint = ProfileSectionAccent
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentProfilePreview() {
    SnehasAcademyTheme { StudentProfileScreen(studentId = "SMAA-STD-001") { } }
}
