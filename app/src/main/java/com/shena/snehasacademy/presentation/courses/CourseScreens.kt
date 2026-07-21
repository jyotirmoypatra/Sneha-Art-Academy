package com.shena.snehasacademy.presentation.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.R
import com.shena.snehasacademy.core.components.DetailInfoRow
import com.shena.snehasacademy.core.components.DetailSectionContainer
import com.shena.snehasacademy.core.components.DetailSectionHeaderRow
import com.shena.snehasacademy.core.components.EmptyState
import com.shena.snehasacademy.core.components.FormSectionCard
import com.shena.snehasacademy.core.components.FormIconField
import com.shena.snehasacademy.core.components.HeaderIconBadge
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ProfileMetaItem
import com.shena.snehasacademy.core.components.ProfileSummaryCard
import com.shena.snehasacademy.core.components.SearchBar
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.SelectablePill
import com.shena.snehasacademy.core.components.SubtitledHeader
import com.shena.snehasacademy.core.theme.AcademyGreen
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Course

private val CourseLevelOptions = listOf("All", "Beginner", "Intermediate", "Advanced")

private val CourseAvatarPalette = listOf(
    Color(0xFF2E8B57) to Color(0xFFDCF3E1),
    Color(0xFF2E6FD9) to Color(0xFFDCEAFB),
    Color(0xFF6C4FC1) to Color(0xFFEAE4FA),
    Color(0xFFD97706) to Color(0xFFFCEBD9)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    viewModel: CourseViewModel? = null,
    onAdd: () -> Unit,
    onOpenDetails: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val courses = viewModel?.courses ?: MockData.courses
    var query by remember { mutableStateOf("") }
    var levelFilter by remember { mutableStateOf("All") }
    var showFilterMenu by remember { mutableStateOf(false) }

    val levelFiltered = remember(levelFilter, courses) {
        if (levelFilter == "All") courses else courses.filter { it.level.equals(levelFilter, ignoreCase = true) }
    }
    val filtered = remember(query, levelFiltered) {
        if (query.isBlank()) {
            levelFiltered
        } else {
            levelFiltered.filter {
                it.title.contains(query, ignoreCase = true) || it.id.contains(query, ignoreCase = true)
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
            SubtitledHeader(title = "All Courses", subtitle = "Manage all academy courses", onBack = onBack) {
                Box {
                    HeaderIconBadge(
                        icon = Icons.Rounded.FilterAlt,
                        tint = AcademyGreen,
                        background = Color.White,
                        contentDescription = "Filter courses",
                        onClick = { showFilterMenu = true }
                    )
                    DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                        CourseLevelOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { levelFilter = option; showFilterMenu = false },
                                trailingIcon = {
                                    if (option == levelFilter) {
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
            FloatingActionButton(onClick = onAdd, containerColor = AcademyGreen, contentColor = Color.White) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Course")
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
                item { SearchBar(query, { query = it }, placeholder = "Search by title or ID...") }
                if (viewModel?.isLoading == true && courses.isEmpty()) {
                    item { LoadingView() }
                } else if (filtered.isEmpty()) {
                    item { EmptyState("No courses found", "Try a different title or course ID.") }
                } else {
                    items(filtered) { course ->
                        CourseRowCard(course, Modifier.padding(top = 2.dp), onClick = { onOpenDetails(course.id) })
                    }
                    item {
                        Text(
                            "Showing ${filtered.size} of ${courses.size} courses",
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
private fun CourseRowCard(course: Course, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val (accent, bg) = remember(course.id) {
        CourseAvatarPalette[(course.id.hashCode() and Int.MAX_VALUE) % CourseAvatarPalette.size]
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
                Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
            }
            Column(
                modifier = Modifier.weight(1f).padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(course.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(course.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "${course.duration.ifBlank { "—" }} • ₹${course.fees}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (course.level.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accent.copy(alpha = 0.14f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(course.level, color = accent, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp).size(20.dp)
            )
        }
    }
}

private val CourseInfoAccent = Color(0xFF6C4FC1)
private val CourseInfoBg = Color(0xFFEAE4FA)
private val PricingAccent = AcademyGreen
private val PricingBg = Color(0xFFDCF3E1)

private fun courseLevelColor(level: String): Color = when (level.trim().lowercase()) {
    "beginner" -> AcademyGreen
    "intermediate" -> Color(0xFFD97706)
    "advanced" -> Color(0xFF6C4FC1)
    else -> Color(0xFF2E6FD9)
}

@Composable
private fun CourseProfileCard(course: Course, duration: String) {
    val accent = courseLevelColor(course.level)
    ProfileSummaryCard(
        avatar = {
            Box(
                modifier = Modifier.size(68.dp).clip(CircleShape).background(CourseInfoBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = null, tint = CourseInfoAccent, modifier = Modifier.size(30.dp))
            }
        },
        title = course.title,
        tagText = "${course.level.ifBlank { "Beginner" }} Level",
        tagColor = accent
    ) {
        ProfileMetaItem(
            Icons.Rounded.Badge, "Course ID", course.id, Modifier.weight(1f),
            iconBg = CourseInfoBg, iconTint = CourseInfoAccent
        )
        VerticalDivider(modifier = Modifier.height(34.dp), color = MaterialTheme.colorScheme.outlineVariant)
        ProfileMetaItem(
            Icons.Rounded.Schedule, "Duration", duration.ifBlank { "—" }, Modifier.weight(1f).padding(start = 10.dp),
            iconBg = PricingBg, iconTint = PricingAccent
        )
    }
}

@Composable
private fun CourseFormHeader(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    action: @Composable () -> Unit = {
        HeaderIconBadge(icon = Icons.AutoMirrored.Rounded.MenuBook, tint = AcademyGreen, background = Color(0xFFDCF3E1))
    }
) {
    SubtitledHeader(title = title, subtitle = subtitle, onBack = onBack, action = action)
}

@Composable
private fun CourseFormFields(
    title: String,
    onTitleChange: (String) -> Unit,
    durationDays: String,
    onDurationChange: (String) -> Unit,
    level: String,
    onLevelChange: (String) -> Unit,
    fees: String,
    onFeesChange: (String) -> Unit
) {
    FormSectionCard(
        icon = Icons.AutoMirrored.Rounded.MenuBook,
        iconBg = CourseInfoBg,
        iconTint = CourseInfoAccent,
        title = "Course Information",
        subtitle = "Enter the course's basic details"
    ) {
        FormIconField(Icons.AutoMirrored.Rounded.MenuBook, CourseInfoBg, CourseInfoAccent, title, onTitleChange, "Course Title *")
        FormIconField(
            Icons.Rounded.Schedule, CourseInfoBg, CourseInfoAccent, durationDays, onDurationChange, "Duration (Days)",
            keyboardType = KeyboardType.Number
        )
        LevelSelector(level, onLevelChange)
    }

    FormSectionCard(
        icon = Icons.Rounded.Payments,
        iconBg = PricingBg,
        iconTint = PricingAccent,
        title = "Pricing",
        subtitle = "Set the course fee"
    ) {
        FormIconField(
            Icons.Rounded.Payments, PricingBg, PricingAccent, fees, onFeesChange, "Course Fees *",
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
private fun LevelSelector(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Level",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            SelectablePill("Beginner", selected == "Beginner", AcademyGreen, Modifier.weight(1f)) { onSelect("Beginner") }
            SelectablePill("Intermediate", selected == "Intermediate", Color(0xFFD97706), Modifier.weight(1f)) { onSelect("Intermediate") }
            SelectablePill("Advanced", selected == "Advanced", Color(0xFF6C4FC1), Modifier.weight(1f)) { onSelect("Advanced") }
        }
    }
}

@Composable
fun AddCourseScreen(viewModel: CourseViewModel? = null, onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var durationDays by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("Beginner") }
    var fees by remember { mutableStateOf("") }

    Scaffold(topBar = { CourseFormHeader("Add Course", "Fill in the details to add a new course", onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CourseFormFields(
                title = title, onTitleChange = { title = it },
                durationDays = durationDays, onDurationChange = { durationDays = it },
                level = level, onLevelChange = { level = it },
                fees = fees, onFeesChange = { fees = it }
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
                    "Save Course",
                    {
                        if (isSaving) return@PrimaryButton
                        val course = Course(
                            title = title,
                            duration = if (durationDays.isNotBlank()) "$durationDays days" else "",
                            level = level,
                            fees = fees.toIntOrNull() ?: 0
                        )
                        if (viewModel != null) {
                            viewModel.addCourse(course) { onBack() }
                        } else {
                            onBack()
                        }
                    },
                    Modifier.weight(2f),
                    enabled = !isSaving,
                    isLoading = isSaving,
                    leadingIcon = Icons.Rounded.Add
                )
            }
        }
    }
}

@Composable
fun CourseDetailsScreen(courseId: String, viewModel: CourseViewModel? = null, onBack: () -> Unit) {
    var loadedCourse by remember(courseId) { mutableStateOf<Course?>(null) }
    var isLoadingCourse by remember(courseId) { mutableStateOf(viewModel != null) }

    LaunchedEffect(courseId, viewModel) {
        if (viewModel != null) {
            isLoadingCourse = true
            loadedCourse = viewModel.getCourse(courseId)
            isLoadingCourse = false
        }
    }

    if (viewModel != null && isLoadingCourse && loadedCourse == null) {
        Scaffold(topBar = { CourseFormHeader("Course Details", "View and manage course information", onBack) }) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val course = loadedCourse ?: MockData.courses.find { it.id == courseId } ?: MockData.courses.first()

    var isEditing by remember { mutableStateOf(false) }
    var title by remember(course.id) { mutableStateOf(course.title) }
    var durationDays by remember(course.id) { mutableStateOf(course.duration.takeWhile { it.isDigit() }) }
    var duration by remember(course.id) { mutableStateOf(course.duration) }
    var level by remember(course.id) { mutableStateOf(course.level.ifBlank { "Beginner" }) }
    var feesText by remember(course.id) { mutableStateOf(course.fees.toString()) }

    fun resetToCourse() {
        title = course.title
        durationDays = course.duration.takeWhile { it.isDigit() }
        level = course.level.ifBlank { "Beginner" }
        feesText = course.fees.toString()
    }

    Scaffold(
        topBar = {
            CourseFormHeader(
                title = if (isEditing) "Edit Course Details" else "Course Details",
                subtitle = "View and manage course information",
                onBack = onBack,
                action = {
                    HeaderIconBadge(
                        painter = painterResource(id = R.drawable.ic_edit),
                        tint = Color(0xFF4B260C),
                        background = Color.White,
                        contentDescription = "Edit",
                        onClick = { isEditing = !isEditing }
                    )
                }
            )
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
            if (isEditing) {
                CourseFormFields(
                    title = title, onTitleChange = { title = it },
                    durationDays = durationDays, onDurationChange = { durationDays = it },
                    level = level, onLevelChange = { level = it },
                    fees = feesText, onFeesChange = { feesText = it }
                )

                viewModel?.errorMessage?.let { message ->
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                val isSaving = viewModel?.isSaving == true
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton("Cancel", { resetToCourse(); isEditing = false }, Modifier.weight(1f))
                    PrimaryButton(
                        "Save",
                        {
                            if (isSaving) return@PrimaryButton
                            val updatedDuration = if (durationDays.isNotBlank()) "$durationDays days" else ""
                            val updated = course.copy(
                                title = title,
                                duration = updatedDuration,
                                level = level,
                                fees = feesText.toIntOrNull() ?: course.fees
                            )
                            if (viewModel != null) {
                                viewModel.updateCourse(updated) {
                                    loadedCourse = updated
                                    duration = updatedDuration
                                    isEditing = false
                                }
                            } else {
                                loadedCourse = updated
                                duration = updatedDuration
                                isEditing = false
                            }
                        },
                        Modifier.weight(2f),
                        enabled = !isSaving,
                        isLoading = isSaving
                    )
                }
            } else {
                CourseProfileCard(course, duration)

                DetailSectionContainer {
                    DetailSectionHeaderRow(Icons.Rounded.Payments, PricingBg, PricingAccent, "Course Details")
                    DetailInfoRow(
                        Icons.Rounded.Schedule, "Duration", duration.ifBlank { "—" },
                        iconBg = CourseInfoBg, iconTint = CourseInfoAccent
                    )
                    DetailInfoRow(
                        Icons.Rounded.Star, "Level", level.ifBlank { "—" },
                        iconBg = CourseInfoBg, iconTint = CourseInfoAccent
                    )
                    DetailInfoRow(
                        Icons.Rounded.Payments, "Course Fees", "₹${course.fees}",
                        iconBg = PricingBg, iconTint = PricingAccent, showDivider = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseListPreview() {
    SnehasAcademyTheme { CourseListScreen(onAdd = {}, onBack = {}) }
}

@Preview(showBackground = true)
@Composable
private fun AddCoursePreview() {
    SnehasAcademyTheme { AddCourseScreen {} }
}

@Preview(showBackground = true)
@Composable
private fun CourseDetailsPreview() {
    SnehasAcademyTheme { CourseDetailsScreen(courseId = "CRS-01", onBack = {}) }
}
