package com.shena.snehasacademy.presentation.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shena.snehasacademy.data.repository.FirestoreAcademyRepository
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.repository.AcademyRepository
import kotlinx.coroutines.launch

class CourseViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                courses = repository.getCourses()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't load courses."
            }
            isLoading = false
        }
    }

    fun addCourse(course: Course, onComplete: () -> Unit) {
        if (isSaving) return
        isSaving = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.addCourse(course)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't save course."
            } finally {
                isSaving = false
            }
        }
    }

    suspend fun getCourse(courseId: String): Course? =
        try {
            repository.getCourse(courseId)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Couldn't load course."
            null
        }

    fun updateCourse(course: Course, onComplete: () -> Unit) {
        if (isSaving) return
        isSaving = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.updateCourse(course)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't update course."
            } finally {
                isSaving = false
            }
        }
    }
}
