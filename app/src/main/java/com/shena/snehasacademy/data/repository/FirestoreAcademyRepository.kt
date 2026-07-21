package com.shena.snehasacademy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shena.snehasacademy.domain.model.Certificate
import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student
import com.shena.snehasacademy.domain.repository.AcademyRepository
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val STUDENTS_COLLECTION = "students"
private const val COURSES_COLLECTION = "courses"
private const val CERTIFICATES_COLLECTION = "certificates"
private const val STUDENT_ID_PREFIX = "SMAA-STD-"
private const val COURSE_ID_PREFIX = "SMAA-CRS-"
private const val CERTIFICATE_ID_PREFIX = "SMAA-CER-"
private val CertificateDateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

class FirestoreAcademyRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AcademyRepository {

    private val studentsRef get() = firestore.collection(STUDENTS_COLLECTION)
    private val coursesRef get() = firestore.collection(COURSES_COLLECTION)
    private val certificatesRef get() = firestore.collection(CERTIFICATES_COLLECTION)

    override suspend fun getStudents(): List<Student> =
        studentsRef.get().await().documents.mapNotNull { it.toObject(Student::class.java) }

    override suspend fun getStudent(studentId: String): Student? =
        studentsRef.document(studentId).get().await().toObject(Student::class.java)

    override suspend fun addStudent(student: Student): String {
        val id = student.id.ifBlank { generateNextStudentId() }
        studentsRef.document(id).set(student.copy(id = id)).await()
        return id
    }

    /**
     * Reads every existing student id, picks the highest "SMAA-STD-###" sequence number, and
     * returns that number + 1 formatted back into the same pattern (zero-padded to 3 digits).
     */
    private suspend fun generateNextStudentId(): String {
        val lastSequence = getStudents()
            .mapNotNull { student ->
                student.id.takeIf { it.startsWith(STUDENT_ID_PREFIX) }
                    ?.removePrefix(STUDENT_ID_PREFIX)
                    ?.toIntOrNull()
            }
            .maxOrNull() ?: 0
        val nextSequence = lastSequence + 1
        return STUDENT_ID_PREFIX + nextSequence.toString().padStart(3, '0')
    }

    override suspend fun updateStudent(student: Student) {
        studentsRef.document(student.id).set(student).await()
    }

    override suspend fun getCourses(): List<Course> =
        coursesRef.get().await().documents.mapNotNull { it.toObject(Course::class.java) }

    override suspend fun getCourse(courseId: String): Course? =
        coursesRef.document(courseId).get().await().toObject(Course::class.java)

    override suspend fun addCourse(course: Course): String {
        val id = course.id.ifBlank { generateNextCourseId() }
        coursesRef.document(id).set(course.copy(id = id)).await()
        return id
    }

    override suspend fun updateCourse(course: Course) {
        coursesRef.document(course.id).set(course).await()
    }

    /**
     * Reads every existing course id, picks the highest "SMAA-CRS-###" sequence number, and
     * returns that number + 1 formatted back into the same pattern (zero-padded to 3 digits).
     */
    private suspend fun generateNextCourseId(): String {
        val lastSequence = getCourses()
            .mapNotNull { course ->
                course.id.takeIf { it.startsWith(COURSE_ID_PREFIX) }
                    ?.removePrefix(COURSE_ID_PREFIX)
                    ?.toIntOrNull()
            }
            .maxOrNull() ?: 0
        val nextSequence = lastSequence + 1
        return COURSE_ID_PREFIX + nextSequence.toString().padStart(3, '0')
    }

    override suspend fun addEnrollment(studentId: String, enrollment: CourseEnrollment) {
        val student = getStudent(studentId) ?: return
        val id = enrollment.id.ifBlank { studentsRef.document().id }
        updateStudent(student.copy(enrollments = student.enrollments + enrollment.copy(id = id)))
    }

    override suspend fun updateEnrollment(studentId: String, enrollment: CourseEnrollment) {
        val student = getStudent(studentId) ?: return
        val updatedEnrollments = student.enrollments.map { if (it.id == enrollment.id) enrollment else it }
        updateStudent(student.copy(enrollments = updatedEnrollments))
    }

    override suspend fun addPayment(studentId: String, enrollmentId: String, payment: Payment) {
        val student = getStudent(studentId) ?: return
        val updatedEnrollments = student.enrollments.map { enrollment ->
            if (enrollment.id == enrollmentId) {
                enrollment.copy(payments = enrollment.payments + payment)
            } else {
                enrollment
            }
        }
        updateStudent(student.copy(enrollments = updatedEnrollments))
    }

    override suspend fun getCertificates(): List<Certificate> =
        certificatesRef.get().await().documents.mapNotNull { it.toObject(Certificate::class.java) }

    override suspend fun getCertificate(enrollmentId: String): Certificate? =
        certificatesRef.whereEqualTo("enrollmentId", enrollmentId).limit(1).get().await()
            .documents.firstOrNull()?.toObject(Certificate::class.java)

    override suspend fun generateCertificate(
        studentId: String,
        enrollmentId: String,
        courseId: String,
        createdBy: String
    ): Certificate {
        // Only one certificate can exist per enrollment — return it as-is if already generated.
        getCertificate(enrollmentId)?.let { return it }

        val id = generateNextCertificateId()
        val certificate = Certificate(
            id = id,
            studentId = studentId,
            enrollmentId = enrollmentId,
            courseId = courseId,
            issueDate = CertificateDateFormatter.format(Date()),
            createdBy = createdBy
        )
        certificatesRef.document(id).set(certificate).await()

        val student = getStudent(studentId)
        if (student != null) {
            val updatedEnrollments = student.enrollments.map {
                if (it.id == enrollmentId) it.copy(certificateId = id) else it
            }
            updateStudent(student.copy(enrollments = updatedEnrollments))
        }

        return certificate
    }

    /**
     * Reads every existing certificate id, picks the highest "SMAA-CER-######" sequence number,
     * and returns that number + 1 formatted back into the same pattern (zero-padded to 6 digits).
     */
    private suspend fun generateNextCertificateId(): String {
        val lastSequence = certificatesRef.get().await().documents
            .mapNotNull { it.toObject(Certificate::class.java)?.id }
            .mapNotNull { id ->
                id.takeIf { it.startsWith(CERTIFICATE_ID_PREFIX) }
                    ?.removePrefix(CERTIFICATE_ID_PREFIX)
                    ?.toIntOrNull()
            }
            .maxOrNull() ?: 0
        val nextSequence = lastSequence + 1
        return CERTIFICATE_ID_PREFIX + nextSequence.toString().padStart(6, '0')
    }
}
