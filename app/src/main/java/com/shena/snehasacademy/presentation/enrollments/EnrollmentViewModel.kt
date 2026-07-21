package com.shena.snehasacademy.presentation.enrollments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.shena.snehasacademy.data.repository.FirestoreAcademyRepository
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import com.shena.snehasacademy.domain.repository.DuplicateEnrollmentException
import kotlinx.coroutines.launch

class EnrollmentViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var students by mutableStateOf<List<Student>>(emptyList())
        private set
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSavingEnrollment by mutableStateOf(false)
        private set
    var isDeletingEnrollment by mutableStateOf(false)
        private set
    var isSavingPayment by mutableStateOf(false)
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
                errorMessage = e.localizedMessage ?: "Couldn't load data."
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

    fun createEnrollment(studentId: String, enrollment: CourseEnrollment, onComplete: () -> Unit) {
        if (isSavingEnrollment) return
        isSavingEnrollment = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.addEnrollment(studentId, enrollment)
                onComplete()
            } catch (e: DuplicateEnrollmentException) {
                errorMessage = e.message
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't create enrollment."
            } finally {
                isSavingEnrollment = false
            }
        }
    }

    fun updateEnrollment(studentId: String, enrollment: CourseEnrollment, onComplete: () -> Unit) {
        if (isSavingEnrollment) return
        isSavingEnrollment = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.updateEnrollment(studentId, enrollment)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't update enrollment."
            } finally {
                isSavingEnrollment = false
            }
        }
    }

    fun deleteEnrollment(studentId: String, enrollmentId: String, onComplete: () -> Unit) {
        if (isDeletingEnrollment) return
        isDeletingEnrollment = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.deleteEnrollment(studentId, enrollmentId)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't delete enrollment."
            } finally {
                isDeletingEnrollment = false
            }
        }
    }

    fun addPayment(studentId: String, enrollmentId: String, payment: Payment, onComplete: () -> Unit) {
        if (isSavingPayment) return
        isSavingPayment = true
        errorMessage = null
        viewModelScope.launch {
            try {
                repository.addPayment(studentId, enrollmentId, payment)
                onComplete()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't add payment."
            } finally {
                isSavingPayment = false
            }
        }
    }

    suspend fun getCertificate(enrollmentId: String): Certificate? =
        try {
            repository.getCertificate(enrollmentId)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Couldn't load certificate."
            null
        }

    fun generateCertificate(studentId: String, enrollmentId: String, courseId: String, onComplete: (Certificate) -> Unit) {
        viewModelScope.launch {
            try {
                val createdBy = FirebaseAuth.getInstance().currentUser?.email ?: "Admin"
                val certificate = repository.generateCertificate(studentId, enrollmentId, courseId, createdBy)
                onComplete(certificate)
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't generate certificate."
            }
        }
    }
}
