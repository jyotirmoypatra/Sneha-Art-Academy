package com.shena.snehasacademy.presentation.students

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Wc
import androidx.compose.material.icons.rounded.WorkspacePremium
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
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.R
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.academyTextFieldColors
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.StatusBadge
import com.shena.snehasacademy.core.components.statusColor
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
private val RegistrationDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

private fun joinedThisMonth(registrationDate: Long): Boolean {
    if (registrationDate <= 0L) return false
    val parsedCal = Calendar.getInstance().apply { timeInMillis = registrationDate }
    val nowCal = Calendar.getInstance()
    return parsedCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
        parsedCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(viewModel: StudentViewModel? = null, onAdd: () -> Unit, onOpenDetails: (String) -> Unit, onBack: () -> Unit) {
    val students = viewModel?.students ?: MockData.students
    val orderedStudents = remember(students) {
        // Newest registrations first; students without a registration date (legacy records) sink to the bottom.
        students.sortedByDescending { it.registrationDate }
    }
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val statusFiltered = remember(statusFilter, orderedStudents) {
        if (statusFilter == "All") orderedStudents else orderedStudents.filter { it.status.equals(statusFilter, ignoreCase = true) }
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
    val newThisMonth = remember(students) { students.count { joinedThisMonth(it.registrationDate) } }

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
                    "All Students",
                    style = MaterialTheme.typography.headlineSmall,
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
                        .size(35.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Rounded.FilterAlt,
                        contentDescription = "Filter students",
                        tint = AcademyGreen,
                        modifier = Modifier.size(18.dp)
                    )
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    val joinedText = if (student.registrationDate > 0L) {
                        RegistrationDateFormatter.format(Date(student.registrationDate))
                    } else {
                        "N/A"
                    }
                    Text(
                        "Joined: $joinedText",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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

    Scaffold(topBar = { StudentFormHeader(onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

            val isSaving = viewModel?.isSaving == true
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton("Cancel", onBack, Modifier.weight(1f))
                PrimaryButton(
                    "Register Student",
                    {
                        if (isSaving) return@PrimaryButton
                        val student = Student(
                            name = fullName,
                            guardianName = guardianName,
                            dateOfBirth = dateOfBirthMillis?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }.orEmpty(),
                            gender = gender,
                            mobile = mobile,
                            address = address,
                            aadhaarNumber = aadhaarNumber,
                            status = "Active",
                            registrationDate = System.currentTimeMillis()
                        )
                        if (viewModel != null) {
                            viewModel.addStudent(student) { onBack() }
                        } else {
                            onBack()
                        }
                    },
                    Modifier.weight(2f),
                    enabled = !isSaving,
                    isLoading = isSaving,
                    leadingIcon = Icons.Rounded.PersonAdd
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
private fun StudentFormHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF7E8))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                "Student Registration",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4B260C)
            )
            Text(
                "Enter student information",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A4A24)
            )
        }
        Box(
            modifier = Modifier.size(35.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFDCF3E1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.School, contentDescription = null, tint = AcademyGreen, modifier = Modifier.size(18.dp))
        }
    }
}

private val BasicInfoAccent = Color(0xFF6C4FC1)
private val BasicInfoBg = Color(0xFFEAE4FA)
private val ContactInfoAccent = AcademyGreen
private val ContactInfoBg = Color(0xFFDCF3E1)
private val IdentityAccent = Color(0xFFD97706)
private val IdentityBg = Color(0xFFFCEBD9)

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
    FormSectionCard(
        icon = Icons.Rounded.Person,
        iconBg = BasicInfoBg,
        iconTint = BasicInfoAccent,
        title = "Basic Information",
        subtitle = "Enter the student's basic details"
    ) {
        FormIconField(Icons.Rounded.Person, BasicInfoBg, BasicInfoAccent, fullName, onFullNameChange, "Full Name *")
        FormIconField(Icons.Rounded.Person, BasicInfoBg, BasicInfoAccent, guardianName, onGuardianNameChange, "Father's / Guardian's Name")
        FormDateField(Icons.Rounded.CalendarMonth, BasicInfoBg, BasicInfoAccent, dateOfBirthMillis, onDateOfBirthClick)
        GenderSelector(gender, onGenderChange)
    }

    FormSectionCard(
        icon = Icons.Rounded.Call,
        iconBg = ContactInfoBg,
        iconTint = ContactInfoAccent,
        title = "Contact Information",
        subtitle = "How can we contact the student?"
    ) {
        FormIconField(
            Icons.Rounded.Call, ContactInfoBg, ContactInfoAccent, mobile, onMobileChange, "Mobile Number *",
            keyboardType = KeyboardType.Phone
        )
        FormIconField(
            Icons.Rounded.LocationOn, ContactInfoBg, ContactInfoAccent, address, onAddressChange, "Address",
            minLines = 3, maxLines = 5
        )
    }

    FormSectionCard(
        icon = Icons.Rounded.Badge,
        iconBg = IdentityBg,
        iconTint = IdentityAccent,
        title = "Identity (Optional)",
        subtitle = "Additional identity information (optional)"
    ) {
        FormIconField(
            Icons.Rounded.Badge, IdentityBg, IdentityAccent, aadhaarNumber, onAadhaarNumberChange, "Aadhaar Number",
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
private fun FormSectionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.width(56.dp).height(2.dp).clip(RoundedCornerShape(1.dp)).background(iconTint))
            }
            content()
        }
    }
}

@Composable
private fun FormIconField(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodySmall) },
        textStyle = MaterialTheme.typography.bodySmall,
        leadingIcon = { FormFieldIconBox(icon, iconBg, iconTint) },
        singleLine = maxLines <= 1,
        minLines = minLines,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = academyTextFieldColors(),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun FormDateField(icon: ImageVector, iconBg: Color, iconTint: Color, dateMillis: Long?, onClick: () -> Unit) {
    val formatted = remember(dateMillis) {
        dateMillis?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) }.orEmpty()
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = formatted,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Date of Birth", style = MaterialTheme.typography.bodySmall) },
            textStyle = MaterialTheme.typography.bodySmall,
            leadingIcon = { FormFieldIconBox(icon, iconBg, iconTint) },
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(13.dp))
                    Text("Select", style = MaterialTheme.typography.labelSmall, color = iconTint, maxLines = 1)
                }
            },
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

@Composable
private fun FormFieldIconBox(icon: ImageVector, iconBg: Color, iconTint: Color) {
    Box(
        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(iconBg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun GenderSelector(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Gender",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            GenderOption(Icons.Rounded.Male, "Male", selected == "Male", BasicInfoAccent, Modifier.weight(1f)) { onSelect("Male") }
            GenderOption(Icons.Rounded.Female, "Female", selected == "Female", Color(0xFFD6336C), Modifier.weight(1f)) { onSelect("Female") }
            GenderOption(Icons.Rounded.Groups, "Other", selected == "Other", AcademyGreen, Modifier.weight(1f)) { onSelect("Other") }
        }
    }
}

@Composable
private fun GenderOption(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) accent.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (selected) accent else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) accent else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private val DobFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailsScreen(
    studentId: String,
    viewModel: StudentViewModel? = null,
    onOpenEnrollment: (String, String) -> Unit = { _, _ -> },
    onEnrollNewCourse: () -> Unit = {},
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
        Scaffold(topBar = { StudentDetailsHeader(isEditing = false, onBack = onBack, onEditClick = {}) }) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val student = loadedStudent ?: MockData.students.find { it.id == studentId } ?: MockData.students.first()
    val courses = viewModel?.courses ?: MockData.courses

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

    Scaffold(
        topBar = { StudentDetailsHeader(isEditing = isEditing, onBack = onBack, onEditClick = { isEditing = !isEditing }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                val isSaving = viewModel?.isSaving == true
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        "Cancel",
                        { resetToStudent(); isEditing = false },
                        Modifier.weight(1f)
                    )
                    PrimaryButton(
                        "Save",
                        {
                            if (isSaving) return@PrimaryButton
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
                                viewModel.updateStudent(updated) {
                                    loadedStudent = updated
                                    isEditing = false
                                }
                            } else {
                                loadedStudent = updated
                                isEditing = false
                            }
                        },
                        Modifier.weight(2f),
                        enabled = !isSaving,
                        isLoading = isSaving
                    )
                }
            } else {
                StudentProfileCard(student, fullName)

                SectionContainer {
                    SectionHeaderRow(Icons.Rounded.Person, Color(0xFFEAE4FA), Color(0xFF6C4FC1), "Personal Information")
                    StudentInfoRow(Icons.Rounded.Person, "Full Name", fullName)
                    StudentInfoRow(Icons.Rounded.Groups, "Father's / Guardian's Name", guardianName)
                    StudentInfoRow(Icons.Rounded.CalendarMonth, "Date of Birth", dateOfBirthDisplay)
                    StudentInfoRow(Icons.Rounded.Wc, "Gender", gender)
                    StudentInfoRow(Icons.Rounded.Call, "Mobile Number", mobile)
                    StudentInfoRow(Icons.Rounded.LocationOn, "Address", address)
                    StudentInfoRow(Icons.Rounded.Badge, "Aadhaar Number", aadhaarNumber, showDivider = false)
                }

                SectionContainer {
                    SectionHeaderRow(
                        Icons.AutoMirrored.Rounded.MenuBook,
                        Color(0xFFEAE4FA),
                        Color(0xFF6C4FC1),
                        "Course Enrollments"
                    ) {
                        Text(
                            "+ Enroll",
                            color = AcademyGreen,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.clickable(onClick = onEnrollNewCourse)
                        )
                    }
                    if (student.enrollments.isEmpty()) {
                        Text("No course enrollments yet.",  style = MaterialTheme.typography.labelSmall,color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        student.enrollments.forEach { enrollment ->
                            val courseId = remember(enrollment.courseId, enrollment.courseName, courses) {
                                enrollment.courseId.ifBlank { courses.find { it.title == enrollment.courseName }?.id.orEmpty() }
                            }

                            Box(
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                CourseEnrollmentCard(
                                    enrollment = enrollment,
                                    courseId = courseId,
                                    onViewCertificate = { onOpenEnrollment(studentId, enrollment.id) }
                                )
                            }
                        }
                    }
                }

                StudentContactActions(mobile)
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
private fun StudentDetailsHeader(isEditing: Boolean, onBack: () -> Unit, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF7E8))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                if (isEditing) "Edit Student Details" else "Student Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF4B260C)
            )
            Text(
                "View and manage student information",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7A4A24)
            )
        }
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit",
                tint = Color(0xFF4B260C),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun StudentProfileCard(student: Student, fullName: String) {
    val initials = remember(fullName) {
        fullName.trim().split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1) }.uppercase()
    }
    val accent = statusColor(student.status)
    val joinedText = if (student.registrationDate > 0L) {
        RegistrationDateFormatter.format(Date(student.registrationDate))
    } else {
        "N/A"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box {
                    Box(
                        modifier = Modifier.size(68.dp).clip(CircleShape).background(Color(0xFFDCF3E1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = AcademyGreen, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(accent)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(Modifier.size(7.dp).clip(CircleShape).background(accent))
                        Text(
                            "${student.status.ifBlank { "Active" }} Student",
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(Modifier.fillMaxWidth()) {
                ProfileMetaItem(Icons.Rounded.School, "Student ID", student.id, Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(34.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileMetaItem(Icons.Rounded.CalendarMonth, "Joined On", joinedText, Modifier.weight(1f).padding(start = 10.dp))
            }
        }
    }
}

@Composable
private fun ProfileMetaItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier.size(26.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFDCF3E1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AcademyGreen, modifier = Modifier.size(13.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(
                value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionContainer(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp), content = content)
    }
}

@Composable
private fun SectionHeaderRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(30.dp).clip(CircleShape).background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
        }
        Text(
            title,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        trailing()
    }
}

@Composable
private fun StudentInfoRow(icon: ImageVector, label: String, value: String, showDivider: Boolean = true) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFDCF3E1)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = AcademyGreen, modifier = Modifier.size(14.dp))
        }
        Text(
            label,
            modifier = Modifier.padding(start = 8.dp).weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    }
}

@Composable
private fun CourseEnrollmentCard(enrollment: CourseEnrollment, courseId: String, onViewCertificate: () -> Unit) {
    val paymentStatus = if (enrollment.fees > 0 && enrollment.amountPaid >= enrollment.fees) "Paid" else "Due"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAE4FA).copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEAE4FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Spa, contentDescription = null, tint = Color(0xFF6C4FC1), modifier = Modifier.size(18.dp))
                }
                Column(Modifier.weight(1f).padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(enrollment.courseName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    if (courseId.isNotBlank()) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color(0xFF6C4FC1).copy(alpha = 0.14f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "Course ID: $courseId",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF6C4FC1),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            EnrollmentMetaRow(Icons.Rounded.CalendarMonth, "Enrollment Date") {
                Text(enrollment.enrollmentDate, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            EnrollmentMetaRow(Icons.Rounded.Star, "Status") { StatusBadge(enrollment.status) }
            EnrollmentMetaRow(Icons.Rounded.Payments, "Fee Status") { StatusBadge(paymentStatus) }
            EnrollmentMetaRow(Icons.Rounded.WorkspacePremium, "Certificate Status") {
                if (enrollment.certificateId.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(onClick = onViewCertificate)
                    ) {
                        Text("View Certificate", color = AcademyGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AcademyGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Text("Not Generated", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun EnrollmentMetaRow(icon: ImageVector, label: String, value: @Composable () -> Unit) {
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

@Composable
private fun StudentContactActions(mobile: String) {
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ContactActionButton(
            icon = Icons.AutoMirrored.Rounded.Chat,
            label = "WhatsApp",
            containerColor = Color(0xFFDCF3E1),
            contentColor = AcademyGreen,
            modifier = Modifier.weight(1f),
            onClick = {
                val digits = mobile.filter { it.isDigit() }
                if (digits.isNotBlank()) {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$digits")))
                    }
                }
            }
        )
        ContactActionButton(
            icon = Icons.Rounded.Call,
            label = "Call Student",
            containerColor = Color(0xFFDCEAFB),
            contentColor = Color(0xFF2E6FD9),
            modifier = Modifier.weight(1f),
            onClick = {
                if (mobile.isNotBlank()) {
                    runCatching {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile")))
                    }
                }
            }
        )
    }
}

@Composable
private fun ContactActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                label,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
