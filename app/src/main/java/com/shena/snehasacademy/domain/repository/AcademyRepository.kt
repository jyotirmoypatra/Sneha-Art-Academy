package com.shena.snehasacademy.domain.repository

import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student

class DuplicateEnrollmentException :
    Exception("This student is already enrolled in the selected course.")

interface AcademyRepository {
    suspend fun getStudents(): List<Student>
    suspend fun getStudent(studentId: String): Student?
    suspend fun addStudent(student: Student): String
    suspend fun updateStudent(student: Student)

    suspend fun getCourses(): List<Course>
    suspend fun getCourse(courseId: String): Course?
    suspend fun addCourse(course: Course): String
    suspend fun updateCourse(course: Course)

    suspend fun addEnrollment(studentId: String, enrollment: CourseEnrollment)
    suspend fun updateEnrollment(studentId: String, enrollment: CourseEnrollment)
    suspend fun deleteEnrollment(studentId: String, enrollmentId: String)
    suspend fun addPayment(studentId: String, enrollmentId: String, payment: Payment)

    suspend fun getCertificates(): List<Certificate>
    suspend fun getCertificate(enrollmentId: String): Certificate?
    suspend fun generateCertificate(studentId: String, enrollmentId: String, courseId: String, createdBy: String): Certificate
    suspend fun deleteCertificate(enrollmentId: String)
}
