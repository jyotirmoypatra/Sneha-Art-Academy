package com.shena.snehasacademy.presentation.auth

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private fun isValidEmail(email: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(email).matches()

class AdminLoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            errorMessage = "Enter your email and password."
            return
        }
        if (!isValidEmail(trimmedEmail)) {
            errorMessage = "Enter a valid email address."
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return
        }
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(trimmedEmail, password).await()
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Login failed. Please try again."
            }
        }
    }
}

class AdminRegisterViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun register(
        name: String,
        mobile: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit
    ) {
        val trimmedEmail = email.trim()
        if (name.isBlank() || trimmedEmail.isBlank() || password.isBlank()) {
            errorMessage = "Fill in your name, email, and password."
            return
        }
        if (!isValidEmail(trimmedEmail)) {
            errorMessage = "Enter a valid email address."
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match."
            return
        }
        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return
        }
        errorMessage = null
        isLoading = true
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
                val uid = result.user?.uid.orEmpty()
                val profile = hashMapOf(
                    "name" to name,
                    "mobile" to mobile,
                    "email" to trimmedEmail
                )
                firestore.collection("admins").document(uid).set(profile).await()
                isLoading = false
                onSuccess()
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.localizedMessage ?: "Registration failed. Please try again."
            }
        }
    }
}

class StudentLoginViewModel : ViewModel()
