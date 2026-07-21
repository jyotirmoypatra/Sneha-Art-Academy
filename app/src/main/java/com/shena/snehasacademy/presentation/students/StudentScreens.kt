package com.shena.snehasacademy.presentation.students

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.LabeledInfo
import com.shena.snehasacademy.core.components.ListItemCard
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.SectionCard
import com.shena.snehasacademy.core.components.SegmentedSelector
import com.shena.snehasacademy.core.components.academyTextFieldColors
import com.shena.snehasacademy.core.components.DateField
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Student
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(viewModel: StudentViewModel? = null, onAdd: () -> Unit, onOpenDetails: (String) -> Unit, onBack: () -> Unit) {
    val students = viewModel?.students ?: MockData.students
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, students) {
        if (query.isBlank()) {
            students
        } else {
            students.filter {
                it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
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

    ScreenScaffold("Students", true, onBack) { contentModifier ->
        PullToRefreshBox(
            isRefreshing = isPullRefreshing,
            onRefresh = {
                isPullRefreshing = true
                viewModel?.refresh()
            },
            modifier = contentModifier.fillMaxSize()
        ) {
            LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { PrimaryButton("Register Student", onAdd) }
                item { SearchBar(query, { query = it }, placeholder = "Search by name or ID") }
                if (viewModel?.isLoading == true && students.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item { EmptyState("No students found", "Try a different name or student ID.") }
                } else {
                    items(filtered) { student ->
                        ListItemCard(
                            student.name,
                            student.id,
                            student.status,
                            Modifier.padding(top = 2.dp),
                            onClick = { onOpenDetails(student.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(viewModel: StudentViewModel? = null, onBack: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var dateOfBirthMillis by remember { mutableStateOf<Long?>(null) }
    var gender by remember { mutableStateOf("Male") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    ScreenScaffold("Student Registration Form", true, onBack) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StudentFormFields(
                fullName = fullName, onFullNameChange = { fullName = it },
                guardianName = guardianName, onGuardianNameChange = { guardianName = it },
                dateOfBirthMillis = dateOfBirthMillis, onDateOfBirthClick = { showDatePicker = true },
                gender = gender, onGenderChange = { gender = it },
                mobile = mobile, onMobileChange = { mobile = it },
                address = address, onAddressChange = { address = it },
                aadhaarNumber = aadhaarNumber, onAadhaarNumberChange = { aadhaarNumber = it }
            )

            viewModel?.errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton("Cancel", onBack, Modifier.weight(1f))
                PrimaryButton(
                    "Register Student",
                    {
                        val student = Student(
                            name = fullName,
                            guardianName = guardianName,
                            dateOfBirth = dateOfBirthMillis?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }.orEmpty(),
                            gender = gender,
                            mobile = mobile,
                            address = address,
                            aadhaarNumber = aadhaarNumber,
                            status = "Active"
                        )
                        if (viewModel != null) {
                            viewModel.addStudent(student) { onBack() }
                        } else {
                            onBack()
                        }
                    },
                    Modifier.weight(2f)
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateOfBirthMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateOfBirthMillis = datePickerState.selectedDateMillis
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
private fun StudentFormFields(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    guardianName: String,
    onGuardianNameChange: (String) -> Unit,
    dateOfBirthMillis: Long?,
    onDateOfBirthClick: () -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    mobile: String,
    onMobileChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    aadhaarNumber: String,
    onAadhaarNumberChange: (String) -> Unit
) {
    SectionCard("Basic Information") {
        ModernTextField(fullName, onFullNameChange, "Full Name *")
        ModernTextField(guardianName, onGuardianNameChange, "Father's / Guardian's Name")
        DateOfBirthField(dateOfBirthMillis, onClick = onDateOfBirthClick)
        SegmentedSelector("Gender", listOf("Male", "Female", "Other"), gender, onGenderChange)
    }

    SectionCard("Contact Information") {
        ModernTextField(mobile, onMobileChange, "Mobile Number *", keyboardType = KeyboardType.Phone)
        MultilineField(address, onAddressChange, "Address")
    }

    SectionCard("Identity (Optional)") {
        ModernTextField(aadhaarNumber, onAadhaarNumberChange, "Aadhaar Number", keyboardType = KeyboardType.Number)
    }
}

@Composable
private fun DateOfBirthField(dateMillis: Long?, onClick: () -> Unit) {
    val formatted = remember(dateMillis) {
        dateMillis?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }.orEmpty()
    }
    DateField(label = "Date of Birth", dateText = formatted, onClick = onClick)
}

@Composable
private fun MultilineField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        shape = RoundedCornerShape(16.dp),
        minLines = 3,
        maxLines = 5,
        colors = academyTextFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

private val DobFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsScreen(
    studentId: String,
    viewModel: StudentViewModel? = null,
    onOpenEnrollment: (String, String) -> Unit = { _, _ -> },
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
        ScreenScaffold("Student Details", true, onBack) { contentModifier ->
            Box(contentModifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val student = loadedStudent ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()

    fun parsedDobMillis() = runCatching { DobFormatter.parse(student.dateOfBirth)?.time }.getOrNull()

    var isEditing by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var fullName by remember(student.id) { mutableStateOf(student.name) }
    var guardianName by remember(student.id) { mutableStateOf(student.guardianName) }
    var dateOfBirthMillis by remember(student.id) { mutableStateOf(parsedDobMillis()) }
    var dateOfBirthDisplay by remember(student.id) { mutableStateOf(student.dateOfBirth) }
    var gender by remember(student.id) { mutableStateOf(student.gender.ifBlank { "Male" }) }
    var mobile by remember(student.id) { mutableStateOf(student.mobile) }
    var address by remember(student.id) { mutableStateOf(student.address) }
    var aadhaarNumber by remember(student.id) { mutableStateOf(student.aadhaarNumber) }

    fun resetToStudent() {
        fullName = student.name
        guardianName = student.guardianName
        dateOfBirthMillis = parsedDobMillis()
        gender = student.gender.ifBlank { "Male" }
        mobile = student.mobile
        address = student.address
        aadhaarNumber = student.aadhaarNumber
    }

    ScreenScaffold(
        title = "Student Details",
        canNavigateBack = true,
        onBack = onBack,
        actions = {
            IconButton(onClick = { isEditing = !isEditing }, modifier = Modifier.size(28.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit",
                    tint = Color(0xFF4B260C),
                    modifier = Modifier.size(18.dp)
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
            Text(
                fullName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            SectionCard("Student ID") {
                Text(student.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            if (isEditing) {
                StudentFormFields(
                    fullName = fullName, onFullNameChange = { fullName = it },
                    guardianName = guardianName, onGuardianNameChange = { guardianName = it },
                    dateOfBirthMillis = dateOfBirthMillis, onDateOfBirthClick = { showDatePicker = true },
                    gender = gender, onGenderChange = { gender = it },
                    mobile = mobile, onMobileChange = { mobile = it },
                    address = address, onAddressChange = { address = it },
                    aadhaarNumber = aadhaarNumber, onAadhaarNumberChange = { aadhaarNumber = it }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton("Cancel", { resetToStudent(); isEditing = false }, Modifier.weight(1f))
                    PrimaryButton(
                        "Save",
                        {
                            dateOfBirthDisplay = dateOfBirthMillis?.let { DobFormatter.format(Date(it)) } ?: dateOfBirthDisplay
                            val updated = student.copy(
                                name = fullName,
                                guardianName = guardianName,
                                dateOfBirth = dateOfBirthDisplay,
                                gender = gender,
                                mobile = mobile,
                                address = address,
                                aadhaarNumber = aadhaarNumber
                            )
                            if (viewModel != null) {
                                viewModel.updateStudent(updated) { loadedStudent = updated }
                            } else {
                                loadedStudent = updated
                            }
                            isEditing = false
                        },
                        Modifier.weight(2f)
                    )
                }
            } else {
                SectionCard("Full Information") {
                    LabeledInfo("Full Name", fullName)
                    LabeledInfo("Father's / Guardian's Name", guardianName)
                    LabeledInfo("Date of Birth", dateOfBirthDisplay)
                    LabeledInfo("Gender", gender)
                    LabeledInfo("Mobile Number", mobile)
                    LabeledInfo("Address", address)
                    LabeledInfo("Aadhaar Number", aadhaarNumber)
                }

                SectionCard("Course Enrollments") {
                    if (student.enrollments.isEmpty()) {
                        Text("No course enrollments yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        student.enrollments.forEach { enrollment ->
                            EnrollmentSummaryRow(
                                enrollment,
                                onViewCertificate = { onOpenEnrollment(studentId, enrollment.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateOfBirthMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateOfBirthMillis = datePickerState.selectedDateMillis
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
private fun EnrollmentSummaryRow(enrollment: CourseEnrollment, onViewCertificate: () -> Unit) {
    val paymentStatus = if (enrollment.fees > 0 && enrollment.amountPaid >= enrollment.fees) "Paid" else "Due"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                enrollment.courseName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            EnrollmentDetailRow("Status") { StatusBadge(enrollment.status) }
            EnrollmentDetailRow("Fee Status") { StatusBadge(paymentStatus) }
            EnrollmentDetailRow("Certificate Status") {
                if (enrollment.certificateId.isNotBlank()) {
                    Text(
                        "View Certificate",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AcademyGreen,
                        modifier = Modifier.clickable(onClick = onViewCertificate)
                    )
                } else {
                    Text(
                        "Not Generated",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EnrollmentDetailRow(label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        value()
    }
}

@Preview(showBackground = true)
@Composable
private fun StudentListPreview() {
    SnehasAcademyTheme { StudentListScreen(onAdd = {}, onOpenDetails = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AddStudentPreview() {
    SnehasAcademyTheme { AddStudentScreen {} }
}

@Preview(showBackground = true)
@Composable
private fun StudentDetailsPreview() {
    SnehasAcademyTheme { StudentDetailsScreen(studentId = "STU-1001", onBack = {}) }
}
