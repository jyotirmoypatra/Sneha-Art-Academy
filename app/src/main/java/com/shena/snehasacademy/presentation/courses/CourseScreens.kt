package com.shena.snehasacademy.presentation.courses

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.shena.snehasacademy.core.components.LabelValueRow
import com.shena.snehasacademy.core.components.LabeledInfo
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.components.SecondaryButton
import com.shena.snehasacademy.core.components.SectionCard
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Course

@Composable
fun CourseListScreen(
    viewModel: CourseViewModel? = null,
    onAdd: () -> Unit,
    onOpenDetails: (String) -> Unit = {},
    onBack: () -> Unit
) {
    val courses = viewModel?.courses ?: MockData.courses

    LifecycleResumeEffect(viewModel) {
        viewModel?.refresh()
        onPauseOrDispose { }
    }

    ScreenScaffold("Courses", true, onBack) { contentModifier ->
        LazyColumn(contentModifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { PrimaryButton("Add Course", onAdd) }
            if (viewModel?.isLoading == true && courses.isEmpty()) {
                item { LoadingView() }
            }
            items(courses) { course ->
                CourseRowCard(course, onClick = { onOpenDetails(course.id) })
            }
        }
    }
}

@Composable
private fun CourseRowCard(course: Course, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(course.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LabelValueRow("Duration", course.duration.ifBlank { "—" })
                LabelValueRow("Course ID", course.id)
            }
        }
    }
}

@Composable
fun AddCourseScreen(viewModel: CourseViewModel? = null, onBack: () -> Unit) {
    ScreenScaffold("Add Course", true, onBack) { contentModifier ->
        Column(contentModifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            var title by remember { mutableStateOf("") }
            var durationDays by remember { mutableStateOf("") }
            var fees by remember { mutableStateOf("") }
            ModernTextField(title, { title = it }, "Course Title")
            ModernTextField(durationDays, { durationDays = it }, "Duration (Days)", keyboardType = KeyboardType.Number)
            ModernTextField(fees, { fees = it }, "Course Fees", keyboardType = KeyboardType.Number)
            viewModel?.errorMessage?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            PrimaryButton(
                "Save Course",
                {
                    val course = Course(
                        title = title,
                        duration = if (durationDays.isNotBlank()) "$durationDays days" else "",
                        fees = fees.toIntOrNull() ?: 0
                    )
                    if (viewModel != null) {
                        viewModel.addCourse(course) { onBack() }
                    } else {
                        onBack()
                    }
                }
            )
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
        ScreenScaffold("Course Details", true, onBack) { contentModifier ->
            Box(contentModifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingView() }
        }
        return
    }

    val course = loadedCourse ?: MockData.courses.find { it.id == courseId } ?: MockData.courses.first()

    var isEditing by remember { mutableStateOf(false) }
    var title by remember(course.id) { mutableStateOf(course.title) }
    var durationDays by remember(course.id) { mutableStateOf(course.duration.takeWhile { it.isDigit() }) }
    var duration by remember(course.id) { mutableStateOf(course.duration) }
    var feesText by remember(course.id) { mutableStateOf(course.fees.toString()) }

    fun resetToCourse() {
        title = course.title
        durationDays = course.duration.takeWhile { it.isDigit() }
        feesText = course.fees.toString()
    }

    ScreenScaffold(
        title = "Course Details",
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
                title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            SectionCard("Course ID") {
                Text(course.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            if (isEditing) {
                SectionCard("Edit Course") {
                    ModernTextField(title, { title = it }, "Course Title")
                    ModernTextField(durationDays, { durationDays = it }, "Duration (Days)", keyboardType = KeyboardType.Number)
                    ModernTextField(feesText, { feesText = it }, "Course Fees", keyboardType = KeyboardType.Number)
                    viewModel?.errorMessage?.let { message ->
                        Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton("Cancel", { resetToCourse(); isEditing = false }, Modifier.weight(1f))
                    PrimaryButton(
                        "Save",
                        {
                            val updatedDuration = if (durationDays.isNotBlank()) "$durationDays days" else ""
                            val updated = course.copy(
                                title = title,
                                duration = updatedDuration,
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
                        Modifier.weight(2f)
                    )
                }
            } else {
                SectionCard("Course Details") {
                    LabeledInfo("Duration", duration)
                    LabeledInfo("Course Fees", "₹${course.fees}")
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
