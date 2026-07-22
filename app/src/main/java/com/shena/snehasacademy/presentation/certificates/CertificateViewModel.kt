package com.shena.snehasacademy.presentation.certificates

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shena.snehasacademy.data.repository.FirestoreAcademyRepository
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import kotlinx.coroutines.launch

class CertificateViewModel(
    private val repository: AcademyRepository = FirestoreAcademyRepository()
) : ViewModel() {
    var certificates by mutableStateOf<List<Certificate>>(emptyList())
        private set
    var students by mutableStateOf<List<Student>>(emptyList())
        private set
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
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
                // Fetch into locals first and assign all three state vars back-to-back once
                // everything has arrived — assigning each as it resolves would recompose the
                // certificate list with some fields still stale/empty ("Unknown Student" etc.)
                // in between awaits.
                val fetchedCertificates = repository.getCertificates()
                val fetchedStudents = repository.getStudents()
                val fetchedCourses = repository.getCourses()
                certificates = fetchedCertificates
                students = fetchedStudents
                courses = fetchedCourses
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Couldn't load certificates."
            }
            isLoading = false
        }
    }
}
