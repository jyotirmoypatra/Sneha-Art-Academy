package com.shena.snehasacademy.data.repository

import com.shena.snehasacademy.core.utils.MockData
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import com.shena.snehasacademy.domain.repository.DuplicateEnrollmentException

/**
 * In-memory repository backed by [MockData]. Used only for Compose previews — production
 * screens use [com.shena.snehasacademy.data.repository.FirestoreAcademyRepository].
 */
class MockAcademyRepository : AcademyRepository {
    override suspend fun getStudents(): List<Student> = MockData.students
    override suspend fun getStudent(studentId: String): Student? = MockData.students.find { it.id == studentId }
    override suspend fun addStudent(student: Student): String = student.id
    override suspend fun updateStudent(student: Student) = Unit

    override suspend fun getCourses(): List<Course> = MockData.courses
    override suspend fun getCourse(courseId: String): Course? = MockData.courses.find { it.id == courseId }
    override suspend fun addCourse(course: Course): String = course.id
    override suspend fun updateCourse(course: Course) = Unit

    override suspend fun addEnrollment(studentId: String, enrollment: CourseEnrollment) {
        val student = getStudent(studentId) ?: return
        if (student.enrollments.any { it.courseName == enrollment.courseName }) {
            throw DuplicateEnrollmentException()
        }
    }
    override suspend fun updateEnrollment(studentId: String, enrollment: CourseEnrollment) = Unit
    override suspend fun deleteEnrollment(studentId: String, enrollmentId: String) = Unit
    override suspend fun addPayment(studentId: String, enrollmentId: String, payment: Payment) = Unit

    override suspend fun getCertificates(): List<Certificate> = emptyList()
    override suspend fun getCertificate(enrollmentId: String): Certificate? = null
    override suspend fun deleteCertificate(enrollmentId: String) = Unit
    override suspend fun generateCertificate(
        studentId: String,
        enrollmentId: String,
        courseId: String,
        createdBy: String
    ): Certificate = Certificate(
        id = "SMAA-CER-000001",
        studentId = studentId,
        enrollmentId = enrollmentId,
        courseId = courseId,
        issueDate = "01 Jan 2026",
        createdBy = createdBy
    )
}
