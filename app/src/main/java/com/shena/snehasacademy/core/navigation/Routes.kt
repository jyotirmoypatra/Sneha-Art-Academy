package com.shena.snehasacademy.core.navigation

sealed class Route(val path: String) {
    data object LoginSelection : Route("login_selection")
    data object AdminLogin : Route("admin_login")
    data object AdminRegister : Route("admin_register")
    data object StudentLogin : Route("student_login")
    data object AdminDashboard : Route("admin_dashboard")
    data object StudentDashboard : Route("student_dashboard/{studentId}") {
        fun createRoute(studentId: String) = "student_dashboard/$studentId"
    }
    data object Students : Route("students")
    data object AddStudent : Route("add_student")
    data object StudentDetails : Route("student_details/{studentId}") {
        fun createRoute(studentId: String) = "student_details/$studentId"
    }
    data object Courses : Route("courses")
    data object AddCourse : Route("add_course")
    data object StudentCourses : Route("student_courses/{studentId}") {
        fun createRoute(studentId: String) = "student_courses/$studentId"
    }
    data object CourseDetails : Route("course_details/{courseId}") {
        fun createRoute(courseId: String) = "course_details/$courseId"
    }
    data object Enrollments : Route("enrollments")
    data object CreateEnrollment : Route("create_enrollment")
    data object EnrollmentDetails : Route("enrollment_details/{studentId}/{enrollmentId}") {
        fun createRoute(studentId: String, enrollmentId: String) = "enrollment_details/$studentId/$enrollmentId"
    }
    data object Attendance : Route("attendance")
    data object Fees : Route("fees")
    data object StudentFees : Route("student_fees/{studentId}") {
        fun createRoute(studentId: String) = "student_fees/$studentId"
    }
    data object PaymentHistory : Route("payment_history/{studentId}/{enrollmentId}") {
        fun createRoute(studentId: String, enrollmentId: String) = "payment_history/$studentId/$enrollmentId"
    }
    data object StudentPaymentHistory : Route("student_payment_history/{studentId}/{enrollmentId}") {
        fun createRoute(studentId: String, enrollmentId: String) = "student_payment_history/$studentId/$enrollmentId"
    }
    data object Certificates : Route("certificates")
    data object StudentCertificates : Route("student_certificates/{studentId}") {
        fun createRoute(studentId: String) = "student_certificates/$studentId"
    }
    data object Settings : Route("settings/{studentId}") {
        fun createRoute(studentId: String) = "settings/$studentId"
    }
}
