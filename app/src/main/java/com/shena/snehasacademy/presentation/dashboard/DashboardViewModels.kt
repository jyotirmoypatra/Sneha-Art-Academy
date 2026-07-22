package com.shena.snehasacademy.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shena.snehasacademy.core.navigation.Route
import com.shena.snehasacademy.data.repository.FirestoreAcademyRepository
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var totalStudents by mutableStateOf(0)
        private set
    var totalCourses by mutableStateOf(0)
        private set
    var totalEnrollments by mutableStateOf(0)
        private set
    var totalCertificates by mutableStateOf(0)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val menu = listOf(
        Route.Students to "Students",
        Route.Courses to "Courses",
        Route.Enrollments to "Enrollments",
      //  Route.Attendance to "Attendance",
        Route.Fees to "Fee Management",
        Route.Certificates to "Certificates"
    )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            try {
                val students = repository.getStudents()
                totalStudents = students.size
                totalCourses = repository.getCourses().size
                totalEnrollments = students.sumOf { it.enrollments.size }
                totalCertificates = repository.getCertificates().size
            } catch (e: Exception) {
                // Leave stats at their last known values; the menu itself doesn't depend on this.
            }
            isLoading = false
        }
    }
}

class StudentDashboardViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var student by mutableStateOf<Student?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val menu = listOf(
        Route.Settings to "My Profile",
        Route.StudentCourses to "My Courses",
        Route.StudentFees to "Fee Status",
        Route.StudentCertificates to "My Certificates"
    )

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            isLoading = true
            student = try {
                repository.getStudent(studentId)
            } catch (e: Exception) {
                null
            }
            isLoading = false
        }
    }

    suspend fun getCertificate(enrollmentId: String): Certificate? =
        try {
            repository.getCertificate(enrollmentId)
        } catch (e: Exception) {
            null
        }
}
