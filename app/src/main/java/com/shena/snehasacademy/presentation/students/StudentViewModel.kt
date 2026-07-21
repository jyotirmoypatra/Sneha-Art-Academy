package com.shena.snehasacademy.presentation.students

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shena.snehasacademy.data.repository.FirestoreAcademyRepository
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList())
        private set
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
                students = repository.getStudents()
                courses = repository.getCourses()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't load students."
            }
            isLoading = false
        }
    }

    suspend fun getStudent(studentId: String): Student? =
        try {
            repository.getStudent(studentId)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Couldn't load student."
            null
        }

    fun addStudent(student: Student, onComplete: () -> Unit) {
        if (isSaving) return
        isSaving = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.addStudent(student)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't save student."
            } finally {
                isSaving = false
            }
        }
    }

    fun updateStudent(student: Student, onComplete: () -> Unit) {
        if (isSaving) return
        isSaving = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.updateStudent(student)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't update student."
            } finally {
                isSaving = false
            }
        }
    }
}
