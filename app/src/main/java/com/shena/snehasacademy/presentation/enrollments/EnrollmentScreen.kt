package com.shena.snehasacademy.presentation.enrollments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.R
import androidx.activity.compose.BackHandler
import com.shena.snehasacademy.core.components.CertificateViewDialog
import com.shena.snehasacademy.presentation.certificates.CertificateFullScreenScreen
import com.shena.snehasacademy.presentation.certificates.CertificateTemplateData
import com.shena.snehasacademy.core.components.DetailInfoRow
import com.shena.snehasacademy.core.components.DetailSectionContainer
import com.shena.snehasacademy.core.components.DetailSectionHeaderRow
import com.shena.snehasacademy.core.components.CheckBadgedAvatar
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.FormFieldIconBox
import com.shena.snehasacademy.core.components.FormIconField
import com.shena.snehasacademy.core.components.FormSectionCard
import com.shena.snehasacademy.core.components.HeaderIconBadge
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ProfileMetaItem
import com.shena.snehasacademy.core.components.ProfileSummaryCard
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.components.StatusCapsuleSelector
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.components.academyTextFieldColors
import com.shena.snehasacademy.core.components.statusColor
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class EnrollmentRow(
    val enrollmentId: String,
    val studentName: String,
    val studentId: String,
    val courseName: String,
    val status: String,
    val enrollmentDate: String
)

private fun flattenEnrollments(students: List<Student>): List<EnrollmentRow> =
    students.flatMap { student ->
        student.enrollments.map { enrollment ->
            EnrollmentRow(enrollment.id, student.name, student.id, enrollment.courseName, enrollment.status, enrollment.enrollmentDate)
        }
    }.sortedByDescending { row ->
        runCatching { EnrollmentDateFormatter.parse(row.enrollmentDate)?.time }.getOrNull() ?: 0L
    }

private val EnrollmentStatusOptions = listOf("All", "Active", "Ongoing", "Completed", "Cancelled")

private val EnrollmentAvatarPalette = listOf(
    Color(0xFF2E8B57) to Color(0xFFDCF3E1),
    Color(0xFF2E6FD9) to Color(0xFFDCEAFB),
    Color(0xFF6C4FC1) to Color(0xFFEAE4FA),
    Color(0xFFD97706) to Color(0xFFFCEBD9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(
    viewModel: EnrollmentViewModel? = null,
    onCreateEnrollment: () -> Unit = {},
    onOpenDetails: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val students = viewModel?.students ?: MockData.students
    val enrollments = remember(students) { flattenEnrollments(students) }
    val statusFiltered = remember(statusFilter, enrollments) {
        if (statusFilter == "All") enrollments else enrollments.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }
    val filtered = remember(query, statusFiltered) {
        if (query.isBlank()) {
            statusFiltered
        } else {
            statusFiltered.filter {
                it.studentId.contains(query, ignoreCase = true) || it.studentName.contains(query, ignoreCase = true)
            }
        }
    }

    val totalEnrollments = enrollments.size
    val activeCount = remember(enrollments) { enrollments.count { it.status.equals("Active", ignoreCase = true) } }
    val completedCount = remember(enrollments) { enrollments.count { it.status.equals("Completed", ignoreCase = true) } }
    val cancelledCount = remember(enrollments) { enrollments.count { it.status.equals("Cancelled", ignoreCase = true) } }

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
            SubtitledHeader(title = "All Enrollments", subtitle = "Manage all student enrollments", onBack = onBack) {
                Box {
                    HeaderIconBadge(
                        icon = Icons.Rounded.FilterAlt,
                        tint = AcademyGreen,
                        background = Color.White,
                        contentDescription = "Filter enrollments",
                        onClick = { showFilterMenu = true }
                    )
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        EnrollmentStatusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { statusFilter = option; showFilterMenu = false },
                                trailingIcon = {
                                    if (option == statusFilter) {
                                        Icon(Icons.Rounded.Check, contentDescription = null, tint = AcademyGreen)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateEnrollment, containerColor = AcademyGreen, contentColor = Color.White) {
                Icon(Icons.Rounded.PersonAdd, contentDescription = "Enroll Student")
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
                contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp)
            ) {
                item { SearchBar(query, { query = it }, placeholder = "Search by student ID or name...") }
                item {
                    EnrollmentStatsRow(
                        total = totalEnrollments,
                        active = activeCount,
                        completed = completedCount,
                        cancelled = cancelledCount
                    )
                }
                if (viewModel?.isLoading == true && enrollments.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item {
                        EmptyState(
                            "No enrollments found",
                            "Try a different student ID or name.",
                            icon = {
                                Icon(
                                    Icons.Rounded.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        )
                    }
                } else {
                    items(filtered) { enrollment ->
                        EnrollmentRowCard(
                            enrollment,
                            Modifier.padding(top = 2.dp),
                            onClick = { onOpenDetails(enrollment.studentId, enrollment.enrollmentId) }
                        )
                    }
                    item {
                        Text(
                            "Showing ${filtered.size} of $totalEnrollments enrollments",
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
}

@Composable
private fun EnrollmentStatsRow(total: Int, active: Int, completed: Int, cancelled: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        EnrollmentStatCard(Icons.AutoMirrored.Rounded.Assignment, Color(0xFFEAE4FA), Color(0xFF6C4FC1), total.toString(), "Total", Modifier.weight(1f))
        EnrollmentStatCard(Icons.Rounded.CheckCircleOutline, Color(0xFFDCF3E1), AcademyGreen, active.toString(), "Active", Modifier.weight(1f))
        EnrollmentStatCard(Icons.Rounded.WorkspacePremium, Color(0xFFDCEAFB), Color(0xFF2E6FD9), completed.toString(), "Completed", Modifier.weight(1f))
        EnrollmentStatCard(Icons.Rounded.Cancel, Color(0xFFFCEBD9), Color(0xFFD97706), cancelled.toString(), "Cancelled", Modifier.weight(1f))
    }
}

@Composable
private fun EnrollmentStatCard(
    icon: ImageVector,
    iconBg: Color,
    accent: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = iconBg.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EnrollmentRowCard(enrollment: EnrollmentRow, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val (accent, bg) = remember(enrollment.studentId) {
        EnrollmentAvatarPalette[(enrollment.studentId.hashCode() and Int.MAX_VALUE) % EnrollmentAvatarPalette.size]
    }
    val initials = remember(enrollment.studentName) {
        enrollment.studentName.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
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
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(enrollment.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(enrollment.studentId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.AutoMirrored.Rounded.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        enrollment.courseName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            StatusBadge(enrollment.status, Modifier.padding(start = 8.dp))
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp).size(20.dp)
            )
        }
    }
}

private val EnrollmentDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
private val EnrollmentInfoAccent = Color(0xFF6C4FC1)
private val EnrollmentInfoBg = Color(0xFFEAE4FA)
private val FeesAccent = AcademyGreen
private val FeesBg = Color(0xFFDCF3E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEnrollmentScreen(viewModel: EnrollmentViewModel? = null, onBack: () -> Unit) {
    val students = remember(viewModel?.students) {
        // Newest registrations first, matching the Student List screen's ordering.
        (viewModel?.students ?: MockData.students).sortedByDescending { it.registrationDate }
    }
    val allCourses = viewModel?.courses ?: MockData.courses
    val studentOptions = remember(students) { students.map { "${it.name} (${it.id})" } }

    var selectedStudentText by remember { mutableStateOf("") }
    var selectedCourseIndex by remember { mutableStateOf(-1) }
    var enrollmentDateMillis by remember { mutableStateOf<Long?>(null) }
    var fees by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val selectedStudent = remember(selectedStudentText, students) {
        students.find { "${it.name} (${it.id})" == selectedStudentText }
    }
    val enrolledCourseIds = remember(selectedStudent, allCourses) {
        selectedStudent?.enrollments
            ?.map { it.courseId.ifBlank { allCourses.find { course -> course.title == it.courseName }?.id.orEmpty() } }
            ?.toSet()
            ?: emptySet()
    }
    val courses = remember(allCourses, enrolledCourseIds) {
        allCourses.filter { it.id !in enrolledCourseIds }
    }
    val courseOptions = remember(courses) { courses.map { it.title } }

    Scaffold(
        topBar = {
            SubtitledHeader(title = "Enroll Student", subtitle = "Fill in the details to enroll a student", onBack = onBack) {
                HeaderIconBadge(icon = Icons.AutoMirrored.Rounded.Assignment, tint = AcademyGreen, background = Color(0xFFDCF3E1))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormSectionCard(
                icon = Icons.AutoMirrored.Rounded.Assignment,
                iconBg = EnrollmentInfoBg,
                iconTint = EnrollmentInfoAccent,
                title = "Enrollment Information",
                subtitle = "Select the student and course to enroll"
            ) {
                StudentPickerField(
                    label = "Select Student",
                    options = studentOptions,
                    selectedText = selectedStudentText,
                    onSelect = {
                        selectedStudentText = it
                        selectedCourseIndex = -1
                        fees = ""
                    }
                )

                DropdownSelectorField(
                    label = "Assign Course",
                    selectedText = selectedCourseIndex.takeIf { it >= 0 }?.let { courseOptions.getOrNull(it) }.orEmpty(),
                    options = courseOptions,
                    onSelect = { index ->
                        selectedCourseIndex = index
                        fees = courses[index].fees.toString()
                    }
                )

                EnrollmentDateField(
                    dateText = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) }.orEmpty(),
                    onClick = { showDatePicker = true }
                )
            }

            FormSectionCard(
                icon = Icons.Rounded.Payments,
                iconBg = FeesBg,
                iconTint = FeesAccent,
                title = "Fees",
                subtitle = "Set the amount payable for this enrollment"
            ) {
                FormIconField(Icons.Rounded.Payments, FeesBg, FeesAccent, fees, { fees = it }, "Fees")
            }

            (validationError ?: viewModel?.errorMessage)?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val isSaving = viewModel?.isSavingEnrollment == true
                SecondaryButton("Cancel", onBack, Modifier.weight(1f))
                PrimaryButton(
                    "Save",
                    {
                        if (isSaving) return@PrimaryButton
                        val selectedStudent = students.find { "${it.name} (${it.id})" == selectedStudentText }
                        val selectedCourse = courses.getOrNull(selectedCourseIndex)
                        if (selectedStudent == null || selectedCourse == null) {
                            validationError = "Select both a student and a course."
                            return@PrimaryButton
                        }
                        validationError = null
                        val enrollment = CourseEnrollment(
                            courseId = selectedCourse.id,
                            courseName = selectedCourse.title,
                            status = "Active",
                            enrollmentDate = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) }.orEmpty(),
                            fees = fees.toIntOrNull() ?: selectedCourse.fees
                        )
                        if (viewModel != null) {
                            viewModel.createEnrollment(selectedStudent.id, enrollment) { onBack() }
                        } else {
                            onBack()
                        }
                    },
                    Modifier.weight(2f),
                    enabled = !isSaving,
                    isLoading = isSaving
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = enrollmentDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    enrollmentDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSelectorField(
    label: String,
    selectedText: String,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label, style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            leadingIcon = { FormFieldIconBox(Icons.AutoMirrored.Rounded.MenuBook, EnrollmentInfoBg, EnrollmentInfoAccent) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            colors = academyTextFieldColors(),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StudentPickerField(
    label: String,
    options: List<String>,
    selectedText: String,
    onSelect: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(label, style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            leadingIcon = { FormFieldIconBox(Icons.AutoMirrored.Rounded.Assignment, EnrollmentInfoBg, EnrollmentInfoAccent) },
            trailingIcon = { Text("Select", style = MaterialTheme.typography.labelSmall, color = EnrollmentInfoAccent) },
            shape = RoundedCornerShape(14.dp),
            colors = academyTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showSheet = true }
                )
        )
    }

    if (showSheet) {
        StudentSearchSheet(
            options = options,
            onDismiss = { showSheet = false },
            onSelect = {
                onSelect(it)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentSearchSheet(
    options: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, options) {
        if (query.isBlank()) options else options.filter { it.contains(query, ignoreCase = true) }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Select Student", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text("Search by name or ID") },
                shape = RoundedCornerShape(16.dp),
                colors = academyTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            if (filtered.isEmpty()) {
                Text(
                    "No students found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 360.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filtered) { option ->
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(option) }
                                .padding(vertical = 12.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentDateField(dateText: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Enrollment Date", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            leadingIcon = { FormFieldIconBox(Icons.Rounded.CalendarMonth, EnrollmentInfoBg, EnrollmentInfoAccent) },
            trailingIcon = { Text("Select", style = MaterialTheme.typography.labelSmall, color = EnrollmentInfoAccent) },
            shape = RoundedCornerShape(14.dp),
            colors = academyTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentDetailsScreen(
    studentId: String,
    enrollmentId: String,
    viewModel: EnrollmentViewModel? = null,
    onBack: () -> Unit
) {
    var loadedStudent by remember(studentId) { mutableStateOf<Student?>(null) }
    var isLoadingStudent by remember(studentId) { mutableStateOf(viewModel != null) }

    LaunchedEffect(studentId, viewModel) {
        if (viewModel != null) {
            isLoadingStudent = true
            loadedStudent = viewModel.getStudent(studentId)
            isLoadingStudent = false
        }
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val isDeleting = viewModel?.isDeletingEnrollment == true

    if (viewModel != null && isLoadingStudent && loadedStudent == null) {
        Scaffold(
            topBar = {
                SubtitledHeader(title = "Enrollment Details", subtitle = "View and manage enrollment information", onBack = onBack)
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val student = loadedStudent ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val enrollment = student.enrollments.find { it.id == enrollmentId } ?: student.enrollments.first()

    val allCourses = viewModel?.courses ?: MockData.courses
    val courseOptions = remember(allCourses) { allCourses.map { it.title } }
    val statusOptions = listOf("Active", "Ongoing", "Completed", "Cancelled")

    fun parsedDateMillis() = runCatching { EnrollmentDateFormatter.parse(enrollment.enrollmentDate)?.time }.getOrNull()

    var isEditingCourse by remember { mutableStateOf(false) }
    var isEditingStatus by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCompletedConfirmation by remember { mutableStateOf(false) }
    var savingSection by remember { mutableStateOf<String?>(null) }
    val isSavingEnrollment = viewModel?.isSavingEnrollment == true
    val isSavingCourse = isSavingEnrollment && savingSection == "course"
    val isSavingStatus = isSavingEnrollment && savingSection == "status"

    var courseName by remember(enrollment.id) { mutableStateOf(enrollment.courseName) }
    var status by remember(enrollment.id) { mutableStateOf(enrollment.status) }
    var enrollmentDateMillis by remember(enrollment.id) { mutableStateOf(parsedDateMillis()) }
    var enrollmentDateDisplay by remember(enrollment.id) { mutableStateOf(enrollment.enrollmentDate) }

    var certificate by remember(enrollment.id) { mutableStateOf<Certificate?>(null) }
    var isLoadingCertificate by remember(enrollment.id) { mutableStateOf(false) }
    var isGeneratingCertificate by remember(enrollment.id) { mutableStateOf(false) }
    var certificateActionError by remember(enrollment.id) { mutableStateOf<String?>(null) }
    var showViewCertificate by remember { mutableStateOf(false) }
    var showCertificatePreview by remember { mutableStateOf(false) }

    val course = remember(courseName, enrollment.courseId, allCourses) {
        val resolvedCourseId = enrollment.courseId.ifBlank { allCourses.find { c -> c.title == courseName }?.id.orEmpty() }
        allCourses.find { it.id == resolvedCourseId } ?: allCourses.find { it.title == courseName }
    }

    if (showCertificatePreview && certificate != null) {
        val cert = certificate!!
        BackHandler { showCertificatePreview = false }
        CertificateFullScreenScreen(
            data = CertificateTemplateData(
                certificateId = cert.id,
                studentId = student.id,
                studentName = student.name,
                courseName = courseName,
                courseDuration = course?.duration.orEmpty(),
                completionDate = cert.issueDate,
                issueDate = cert.issueDate
            ),
            onBack = { showCertificatePreview = false }
        )
        return
    }

    val initials = remember(student.name) {
        student.name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }

    LaunchedEffect(enrollment.id, enrollment.status, viewModel) {
        if (viewModel != null && enrollment.status == "Completed") {
            isLoadingCertificate = true
            certificate = viewModel.getCertificate(enrollment.id)
            isLoadingCertificate = false
        }
    }

    fun persistEnrollment(updated: CourseEnrollment, onDone: () -> Unit) {
        val currentStudent = loadedStudent
        if (viewModel != null && currentStudent != null) {
            viewModel.updateEnrollment(studentId, updated) {
                loadedStudent = currentStudent.copy(
                    enrollments = currentStudent.enrollments.map { if (it.id == updated.id) updated else it }
                )
                onDone()
            }
        } else {
            onDone()
        }
    }

    Scaffold(
        topBar = {
            SubtitledHeader(title = "Enrollment Details", subtitle = "View and manage enrollment information", onBack = onBack) {
                HeaderIconBadge(
                    icon = Icons.Filled.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    background = Color.White,
                    contentDescription = "Delete Enrollment",
                    onClick = { if (!isDeleting) showDeleteConfirmation = true }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileSummaryCard(
                avatar = {
                    CheckBadgedAvatar(
                        initialsOrIcon = {
                            Text(initials, color = AcademyGreen, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                        },
                        badgeColor = statusColor(status)
                    )
                },
                title = student.name,
                tagText = status,
                tagColor = statusColor(status)
            ) {
                ProfileMetaItem(
                    Icons.AutoMirrored.Rounded.Assignment, "Student ID", student.id, Modifier.weight(1f),
                    iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent
                )
                VerticalDivider(modifier = Modifier.height(34.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMetaItem(
                    Icons.AutoMirrored.Rounded.MenuBook, "Course", courseName, Modifier.weight(1f).padding(start = 10.dp),
                    iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent
                )
            }

            DetailSectionContainer {
                DetailSectionHeaderRow(
                    Icons.AutoMirrored.Rounded.MenuBook,
                    EnrollmentInfoBg,
                    EnrollmentInfoAccent,
                    "Course Details"
                ) {
                    HeaderIconBadge(
                        painter = painterResource(id = R.drawable.ic_edit),
                        tint = Color(0xFF4B260C),
                        background = Color(0xFFFFF7E8),
                        contentDescription = "Edit",
                        onClick = {
                            if (isEditingCourse) {
                                courseName = enrollment.courseName
                                enrollmentDateMillis = parsedDateMillis()
                                isEditingCourse = false
                            } else {
                                if (isEditingStatus) {
                                    status = enrollment.status
                                    isEditingStatus = false
                                }
                                isEditingCourse = true
                            }
                        }
                    )
                }

                if (isEditingCourse) {
                    DropdownSelectorField(
                        label = "Course",
                        selectedText = courseName,
                        options = courseOptions,
                        onSelect = { courseName = courseOptions[it] }
                    )
                    EnrollmentDateField(
                        dateText = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) }.orEmpty(),
                        onClick = { showDatePicker = true }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            "Cancel",
                            {
                                courseName = enrollment.courseName
                                enrollmentDateMillis = parsedDateMillis()
                                isEditingCourse = false
                            },
                            Modifier.weight(1f),
                            height = 40.dp
                        )
                        PrimaryButton(
                            "Save",
                            {
                                if (isSavingEnrollment) return@PrimaryButton
                                savingSection = "course"
                                val updatedDate = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) } ?: enrollment.enrollmentDate
                                val updated = enrollment.copy(
                                    courseId = allCourses.find { it.title == courseName }?.id ?: enrollment.courseId,
                                    courseName = courseName,
                                    enrollmentDate = updatedDate
                                )
                                persistEnrollment(updated) {
                                    enrollmentDateDisplay = updatedDate
                                    isEditingCourse = false
                                    savingSection = null
                                }
                            },
                            Modifier.weight(1f),
                            enabled = !isSavingEnrollment,
                            isLoading = isSavingCourse,
                            height = 40.dp
                        )
                    }
                } else {
                    DetailInfoRow(
                        Icons.AutoMirrored.Rounded.MenuBook, "Course", courseName,
                        iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent
                    )
                    course?.let {
                        DetailInfoRow(
                            Icons.Rounded.CalendarMonth, "Duration", it.duration,
                            iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent
                        )
                    }
                    DetailInfoRow(
                        Icons.Rounded.CalendarMonth, "Enrollment Date", enrollmentDateDisplay,
                        iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent, showDivider = false
                    )
                }
            }

            DetailSectionContainer {
                DetailSectionHeaderRow(Icons.Rounded.CheckCircleOutline, FeesBg, FeesAccent, "Status") {
                    HeaderIconBadge(
                        painter = painterResource(id = R.drawable.ic_edit),
                        tint = Color(0xFF4B260C),
                        background = Color(0xFFFFF7E8),
                        contentDescription = "Edit",
                        onClick = {
                            if (isEditingStatus) {
                                status = enrollment.status
                                isEditingStatus = false
                            } else {
                                if (isEditingCourse) {
                                    courseName = enrollment.courseName
                                    enrollmentDateMillis = parsedDateMillis()
                                    isEditingCourse = false
                                }
                                isEditingStatus = true
                            }
                        }
                    )
                }

                if (isEditingStatus) {
                    StatusCapsuleSelector(statusOptions, status) { status = it }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryButton(
                            "Cancel",
                            {
                                status = enrollment.status
                                isEditingStatus = false
                            },
                            Modifier.weight(1f),
                            height = 40.dp
                        )
                        PrimaryButton(
                            "Save",
                            {
                                if (isSavingEnrollment) return@PrimaryButton
                                if (status == "Completed") {
                                    showCompletedConfirmation = true
                                } else {
                                    savingSection = "status"
                                    val updated = enrollment.copy(status = status)
                                    persistEnrollment(updated) {
                                        isEditingStatus = false
                                        savingSection = null
                                    }
                                }
                            },
                            Modifier.weight(1f),
                            enabled = !isSavingEnrollment,
                            isLoading = isSavingStatus,
                            height = 40.dp
                        )
                    }
                } else {
                    StatusBadge(status)
                }
            }

            if (enrollment.status == "Completed") {
                DetailSectionContainer {
                    DetailSectionHeaderRow(Icons.Rounded.WorkspacePremium, EnrollmentInfoBg, EnrollmentInfoAccent, "Certificate")
                    when {
                        isLoadingCertificate -> LoadingView()
                        certificate != null -> {
                            DetailInfoRow(
                                Icons.Rounded.WorkspacePremium, "Certificate ID", certificate!!.id,
                                iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent
                            )
                            DetailInfoRow(
                                Icons.Rounded.CalendarMonth, "Issue Date", certificate!!.issueDate,
                                iconBg = EnrollmentInfoBg, iconTint = EnrollmentInfoAccent, showDivider = false
                            )
                            PrimaryButton(
                                "View Certificate",
                                { showViewCertificate = true },
                                Modifier.padding(top = 8.dp),
                                height = 40.dp
                            )
                        }
                        else -> {
                            (certificateActionError ?: viewModel?.errorMessage)?.let { message ->
                                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            PrimaryButton(
                                text = if (isGeneratingCertificate) "Generating..." else "Generate Certificate",
                                enabled = !isGeneratingCertificate,
                                onClick = {
                                    if (viewModel == null || isGeneratingCertificate) return@PrimaryButton
                                    val courseIdForCertificate = enrollment.courseId.ifBlank { course?.id.orEmpty() }
                                    if (courseIdForCertificate.isBlank()) {
                                        certificateActionError = "Course details are still loading. Please try again in a moment."
                                        return@PrimaryButton
                                    }
                                    certificateActionError = null
                                    isGeneratingCertificate = true
                                    viewModel.generateCertificate(studentId, enrollment.id, courseIdForCertificate) { generated ->
                                        certificate = generated
                                        isGeneratingCertificate = false
                                    }
                                },
                                height = 40.dp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCompletedConfirmation) {
        AlertDialog(
            onDismissRequest = { showCompletedConfirmation = false },
            title = { Text("Mark as Completed") },
            text = { Text("Are you sure this enrollment course is completed?") },
            confirmButton = {
                TextButton(onClick = {
                    showCompletedConfirmation = false
                    savingSection = "status"
                    val updated = enrollment.copy(status = status)
                    persistEnrollment(updated) {
                        isEditingStatus = false
                        savingSection = null
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showCompletedConfirmation = false }) { Text("No") }
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteConfirmation = false },
            title = { Text("Delete Enrollment") },
            text = { Text("Are you sure you want to delete this enrollment? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        if (viewModel != null) {
                            viewModel.deleteEnrollment(studentId, enrollment.id) {
                                showDeleteConfirmation = false
                                onBack()
                            }
                        } else {
                            showDeleteConfirmation = false
                            onBack()
                        }
                    }
                ) { Text(if (isDeleting) "Deleting..." else "Delete") }
            },
            dismissButton = {
                TextButton(enabled = !isDeleting, onClick = { showDeleteConfirmation = false }) { Text("Cancel") }
            }
        )
    }

    if (showViewCertificate && certificate != null) {
        val cert = certificate!!
        CertificateViewDialog(
            certificateId = cert.id,
            studentName = student.name,
            studentId = student.id,
            courseName = courseName,
            issueDate = cert.issueDate,
            createdBy = cert.createdBy,
            onDismiss = { showViewCertificate = false },
            onViewPreview = {
                showViewCertificate = false
                showCertificatePreview = true
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = enrollmentDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    enrollmentDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EnrollmentPreview() {
    SnehasAcademyTheme { EnrollmentScreen(onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun CreateEnrollmentPreview() {
    SnehasAcademyTheme { CreateEnrollmentScreen {} }
}

@Preview(showBackground = true)
@Composable
private fun EnrollmentDetailsPreview() {
    SnehasAcademyTheme { EnrollmentDetailsScreen(studentId = "STU-1001", enrollmentId = "ENR-0001", onBack = {}) }
}
