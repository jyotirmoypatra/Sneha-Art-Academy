package com.shena.snehasacademy.domain.model

// Every field has a default so Firestore's automatic POJO mapping can use a no-arg constructor.
data class Student(
    val id: String = "",
    val name: String = "",
    val courseName: String = "",
    val status: String = "",
    val guardianName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val mobile: String = "",
    val address: String = "",
    val aadhaarNumber: String = "",
    // Set once, automatically, when the student is registered — epoch millis. Never edited afterward.
    val registrationDate: Long = 0L,
    val enrollments: List<CourseEnrollment> = emptyList()
)

data class CourseEnrollment(
    val id: String = "",
    val courseName: String = "",
    val status: String = "",
    val enrollmentDate: String = "",
    val fees: Int = 0,
    val payments: List<Payment> = emptyList(),
    val certificateId: String = ""
) {
    val amountPaid: Int get() = payments.sumOf { it.amount }
}

data class Payment(
    val id: String = "",
    val amount: Int = 0,
    val date: String = ""
)

data class Certificate(
    val id: String = "",
    val studentId: String = "",
    val enrollmentId: String = "",
    val courseId: String = "",
    val issueDate: String = "",
    val createdBy: String = ""
)

data class Course(
    val id: String = "",
    val title: String = "",
    val duration: String = "",
    val level: String = "",
    val fees: Int = 0
)

data class UserProfile(
    val name: String,
    val role: String,
    val email: String
)
