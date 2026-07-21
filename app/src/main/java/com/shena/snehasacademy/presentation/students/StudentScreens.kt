package com.shena.snehasacademy.presentation.students

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val StudentFilterOptions = listOf("All", "Active", "Inactive")

private fun joinedThisMonth(joinDate: String): Boolean {
    if (joinDate.isBlank()) return false
    val parsed = runCatching { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(joinDate) }.getOrNull() ?: return false
    val parsedCal = Calendar.getInstance().apply { time = parsed }
    val nowCal = Calendar.getInstance()
    return parsedCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
        parsedCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(viewModel: StudentViewModel? = null, onAdd: () -> Unit, onOpenDetails: (String) -> Unit, onBack: () -> Unit) {
    val students = viewModel?.students ?: MockData.students
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val statusFiltered = remember(statusFilter, students) {
        if (statusFilter == "All") students else students.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }
    val filtered = remember(query, statusFiltered) {
        if (query.isBlank()) {
            statusFiltered
        } else {
            statusFiltered.filter {
                it.name.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
            }
        }
    }

    val totalStudents = students.size
    val activeStudents = remember(students) { students.count { it.status.equals("Active", ignoreCase = true) } }
    val inactiveStudents = totalStudents - activeStudents
    val newThisMonth = remember(students) { students.count { joinedThisMonth(it.joinDate) } }

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
            StudentsHeader(
                onBack = onBack,
                showFilterMenu = showFilterMenu,
                onFilterClick = { showFilterMenu = true },
                onDismissFilterMenu = { showFilterMenu = false },
                selectedFilter = statusFilter,
                onSelectFilter = {
                    statusFilter = it
                    showFilterMenu = false
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd, containerColor = AcademyGreen, contentColor = Color.White) {
                Icon(Icons.Rounded.PersonAdd, contentDescription = "Register Student")
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
                item { PrimaryButton("Register New Student", onAdd, leadingIcon = Icons.Rounded.PersonAdd) }
                item { SearchBar(query, { query = it }, placeholder = "Search by name or ID...") }
                item {
                    StudentStatsRow(
                        total = totalStudents,
                        active = activeStudents,
                        inactive = inactiveStudents,
                        newThisMonth = newThisMonth
                    )
                }
                if (viewModel?.isLoading == true && students.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item { EmptyState("No students found", "Try a different name or student ID.") }
                } else {
                    items(filtered) { student ->
                        StudentRowCard(student, Modifier.padding(top = 2.dp), onClick = { onOpenDetails(student.id) })
                    }
                    item {
                        Text(
                            "Showing ${filtered.size} of $totalStudents students",
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
private fun StudentsHeader(
    onBack: () -> Unit,
    showFilterMenu: Boolean,
    onFilterClick: () -> Unit,
    onDismissFilterMenu: () -> Unit,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF7E8))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color(0xFF4B260C),
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(Modifier.weight(1f).padding(start = 6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "Students",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4B260C)
                )
                Text(
                    "Manage all academy students",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7A4A24)
                )
            }
            Box {
                IconButton(
                    onClick = onFilterClick,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                ) {
                    Icon(Icons.Rounded.FilterAlt, contentDescription = "Filter students", tint = AcademyGreen)
                }
                DropdownMenu(expanded = showFilterMenu, onDismissRequest = onDismissFilterMenu) {
                    StudentFilterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { onSelectFilter(option) },
                            trailingIcon = {
                                if (option == selectedFilter) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = AcademyGreen)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentStatsRow(total: Int, active: Int, inactive: Int, newThisMonth: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StudentStatCard(Icons.Rounded.Groups, Color(0xFFDCF3E1), AcademyGreen, total.toString(), "Total Students", Modifier.weight(1f))
        StudentStatCard(Icons.Rounded.School, Color(0xFFDCEAFB), Color(0xFF2E6FD9), active.toString(), "Active Students", Modifier.weight(1f))
        StudentStatCard(Icons.Rounded.Person, Color(0xFFFCEBD9), Color(0xFFD97706), inactive.toString(), "Inactive Students", Modifier.weight(1f))
        StudentStatCard(Icons.Rounded.CalendarMonth, Color(0xFFEAE4FA), Color(0xFF6C4FC1), newThisMonth.toString(), "New This Month", Modifier.weight(1f))
    }
}

@Composable
private fun StudentStatCard(
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
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private val StudentAvatarPalette = listOf(
    Color(0xFF2E8B57) to Color(0xFFDCF3E1),
    Color(0xFF2E6FD9) to Color(0xFFDCEAFB),
    Color(0xFF6C4FC1) to Color(0xFFEAE4FA),
    Color(0xFFD97706) to Color(0xFFFCEBD9)
)

@Composable
private fun StudentRowCard(student: Student, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val (accent, bg) = remember(student.id) {
        StudentAvatarPalette[(student.id.hashCode() and Int.MAX_VALUE) % StudentAvatarPalette.size]
    }
    val initials = remember(student.name) {
        student.name.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
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
                Text(student.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(student.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (student.joinDate.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            Icons.Rounded.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "Joined ${student.joinDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            StatusBadge(student.status, Modifier.padding(start = 8.dp))
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 2.dp).size(20.dp)
            )
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
                            status = "Active",
                            joinDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
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
