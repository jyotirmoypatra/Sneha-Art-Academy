package com.shena.snehasacademy.presentation.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.shena.snehasacademy.core.components.ListItemCard
import com.shena.snehasacademy.core.components.LoadingView
import com.shena.snehasacademy.core.components.ModernTextField
import com.shena.snehasacademy.core.components.PrimaryButton
import com.shena.snehasacademy.core.components.ScreenScaffold
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Course

@Composable
fun CourseListScreen(viewModel: CourseViewModel? = null, onAdd: () -> Unit, onBack: () -> Unit) {
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
                ListItemCard(course.title, "${course.duration} • ${course.level}", course.id)
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
