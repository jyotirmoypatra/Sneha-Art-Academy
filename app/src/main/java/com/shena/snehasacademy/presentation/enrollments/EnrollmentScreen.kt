package com.shena.snehasacademy.presentation.enrollments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.R
import com.shena.snehasacademy.core.components.CertificateViewDialog
import com.shena.snehasacademy.core.components.DateField
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.LabeledInfo
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.SectionCard
import com.shena.snehasacademy.core.components.StatusCapsuleSelector
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.components.academyTextFieldColors
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
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(
    viewModel: EnrollmentViewModel? = null,
    onCreateEnrollment: () -> Unit = {},
    onOpenDetails: (String, String) -> Unit = { _, _ -> },
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val students = viewModel?.students ?: MockData.students
    val enrollments = remember(students) { flattenEnrollments(students) }
    val filtered = remember(query, enrollments) {
        if (query.isBlank()) {
            enrollments
        } else {
            enrollments.filter {
                it.studentId.contains(query, ignoreCase = true) || it.studentName.contains(query, ignoreCase = true)
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

    ScreenScaffold("Enrollments", true, onBack) { contentModifier ->
        PullToRefreshBox(
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                viewModel?.refresh()
            },
            modifier = contentModifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { PrimaryButton("Enroll Student", onCreateEnrollment) }
                item { SearchBar(query, { query = it }, placeholder = "Search by student ID or name") }
                if (viewModel?.isLoading == true && enrollments.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item { EmptyState("No enrollments found", "Try a different student ID or name.") }
                } else {
                    items(filtered) { enrollment ->
                        EnrollmentRowCard(
                            enrollment,
                            Modifier.padding(top = 2.dp),
                            onClick = { onOpenDetails(enrollment.studentId, enrollment.enrollmentId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentRowCard(enrollment: EnrollmentRow, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(enrollment.studentName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(enrollment.studentId, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(enrollment.courseName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(enrollment.status, Modifier.padding(start = 8.dp))
        }
    }
}

private val EnrollmentDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEnrollmentScreen(viewModel: EnrollmentViewModel? = null, onBack: () -> Unit) {
    val students = viewModel?.students ?: MockData.students
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
    val enrolledCourseNames = remember(selectedStudent) {
        selectedStudent?.enrollments?.map { it.courseName }?.toSet() ?: emptySet()
    }
    val courses = remember(allCourses, enrolledCourseNames) {
        allCourses.filter { it.title !in enrolledCourseNames }
    }
    val courseOptions = remember(courses) { courses.map { it.title } }

    ScreenScaffold("Enroll Student", true, onBack) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            ModernTextField(fees, { fees = it }, "Fees", keyboardType = KeyboardType.Number)

            EnrollmentDateField(
                dateText = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) }.orEmpty(),
                onClick = { showDatePicker = true }
            )

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
            label = { Text(label) },
            placeholder = { Text("Select") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(16.dp),
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
            label = { Text(label) },
            placeholder = { Text("Select") },
            trailingIcon = { Text("Select", style = MaterialTheme.typography.labelMedium) },
            shape = RoundedCornerShape(16.dp),
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
    DateField(label = "Enrollment Date", dateText = dateText, onClick = onClick)
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

    if (viewModel != null && isLoadingStudent && loadedStudent == null) {
        ScreenScaffold("Enrollment Details", true, onBack) { contentModifier ->
            Box(contentModifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingView() }
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
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val isDeleting = viewModel?.isDeletingEnrollment == true

    var courseName by remember(enrollment.id) { mutableStateOf(enrollment.courseName) }
    var status by remember(enrollment.id) { mutableStateOf(enrollment.status) }
    var enrollmentDateMillis by remember(enrollment.id) { mutableStateOf(parsedDateMillis()) }
    var enrollmentDateDisplay by remember(enrollment.id) { mutableStateOf(enrollment.enrollmentDate) }

    var certificate by remember(enrollment.id) { mutableStateOf<Certificate?>(null) }
    var isLoadingCertificate by remember(enrollment.id) { mutableStateOf(false) }
    var isGeneratingCertificate by remember(enrollment.id) { mutableStateOf(false) }
    var showViewCertificate by remember { mutableStateOf(false) }

    val course = remember(courseName, allCourses) { allCourses.find { it.title == courseName } }

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

    ScreenScaffold(
        title = "Enrollment Details",
        canNavigateBack = true,
        onBack = onBack,
        actions = {
            IconButton(
                onClick = { showDeleteConfirmation = true },
                enabled = !isDeleting,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Enrollment",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    student.name,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    student.id,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionCard(
                title = "Course Details",
                actions = { EditPencilButton(onClick = { isEditingCourse = !isEditingCourse }) }
            ) {
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton(
                            "Cancel",
                            {
                                courseName = enrollment.courseName
                                enrollmentDateMillis = parsedDateMillis()
                                isEditingCourse = false
                            },
                            Modifier.weight(1f)
                        )
                        PrimaryButton(
                            "Save",
                            {
                                val updatedDate = enrollmentDateMillis?.let { EnrollmentDateFormatter.format(Date(it)) } ?: enrollment.enrollmentDate
                                val updated = enrollment.copy(courseName = courseName, enrollmentDate = updatedDate)
                                persistEnrollment(updated) {
                                    enrollmentDateDisplay = updatedDate
                                    isEditingCourse = false
                                }
                            },
                            Modifier.weight(2f)
                        )
                    }
                } else {
                    LabeledInfo("Course", courseName)
                    course?.let {
                        LabeledInfo("Duration", it.duration)
                    }
                    LabeledInfo("Enrollment Date", enrollmentDateDisplay)
                }
            }

            SectionCard(
                title = "Status",
                actions = { EditPencilButton(onClick = { isEditingStatus = !isEditingStatus }) }
            ) {
                if (isEditingStatus) {
                    StatusCapsuleSelector(statusOptions, status) { status = it }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SecondaryButton(
                            "Cancel",
                            {
                                status = enrollment.status
                                isEditingStatus = false
                            },
                            Modifier.weight(1f)
                        )
                        PrimaryButton(
                            "Save",
                            {
                                if (status == "Completed") {
                                    showCompletedConfirmation = true
                                } else {
                                    val updated = enrollment.copy(status = status)
                                    persistEnrollment(updated) { isEditingStatus = false }
                                }
                            },
                            Modifier.weight(2f)
                        )
                    }
                } else {
                    StatusBadge(status)
                }
            }

            if (enrollment.status == "Completed") {
                SectionCard(title = "Certificate") {
                    when {
                        isLoadingCertificate -> LoadingView()
                        certificate != null -> {
                            LabeledInfo("Certificate ID", certificate!!.id)
                            LabeledInfo("Issue Date", certificate!!.issueDate)
                            PrimaryButton("View Certificate", { showViewCertificate = true })
                        }
                        else -> {
                            viewModel?.errorMessage?.let { message ->
                                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }
                            PrimaryButton(
                                text = if (isGeneratingCertificate) "Generating..." else "Generate Certificate",
                                enabled = !isGeneratingCertificate,
                                onClick = {
                                    val currentCourse = course
                                    if (viewModel == null || currentCourse == null || isGeneratingCertificate) return@PrimaryButton
                                    isGeneratingCertificate = true
                                    viewModel.generateCertificate(studentId, enrollment.id, currentCourse.id) { generated ->
                                        certificate = generated
                                        isGeneratingCertificate = false
                                    }
                                }
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
                    val updated = enrollment.copy(status = status)
                    persistEnrollment(updated) { isEditingStatus = false }
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
            onDismiss = { showViewCertificate = false }
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

@Composable
private fun EditPencilButton(onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(26.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_edit),
            contentDescription = "Edit",
            tint = Color(0xFF4B260C),
            modifier = Modifier.size(16.dp)
        )
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
