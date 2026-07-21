package com.shena.snehasacademy.core.utils

import com.shena.snehasacademy.domain.model.Course
import com.shena.snehasacademy.domain.model.CourseEnrollment
import com.shena.snehasacademy.domain.model.Payment
import com.shena.snehasacademy.domain.model.Student

object MockData {
    val students = listOf(
        Student(
            id = "STU-1001",
            name = "Aarohi Sharma",
            courseName = "Bridal Mehendi",
            status = "Active",
            guardianName = "Ramesh Sharma",
            dateOfBirth = "12 Jan 2005",
            gender = "Female",
            mobile = "9876543210",
            address = "24, Lotus Lane, Bhubaneswar",
            aadhaarNumber = "XXXX-XXXX-1234",
            enrollments = listOf(
                CourseEnrollment(
                    "ENR-0001", "Bridal Mehendi Masterclass", "Active", "10 Jan 2026", fees = 15000,
                    payments = listOf(
                        Payment("PAY-0001", 5000, "10 Jan 2026"),
                        Payment("PAY-0002", 5000, "10 Feb 2026")
                    )
                ),
                CourseEnrollment(
                    "ENR-0002", "Fine Art Foundations", "Completed", "02 Mar 2025", fees = 9000,
                    payments = listOf(Payment("PAY-0003", 9000, "02 Mar 2025"))
                )
            )
        ),
        Student(
            id = "STU-1002",
            name = "Meera Das",
            courseName = "Arabic Patterns",
            status = "Active",
            guardianName = "Sunil Das",
            dateOfBirth = "03 Jun 2006",
            gender = "Female",
            mobile = "9876501234",
            address = "12, Green Park, Cuttack",
            aadhaarNumber = "XXXX-XXXX-5678",
            enrollments = listOf(
                CourseEnrollment(
                    "ENR-0003", "Arabic Mehendi Basics", "Active", "15 Feb 2026", fees = 6000,
                    payments = listOf(Payment("PAY-0004", 3000, "15 Feb 2026"))
                )
            )
        ),
        Student(
            id = "STU-1003",
            name = "Kavya Iyer",
            courseName = "Portrait Sketching",
            status = "Due",
            guardianName = "Suresh Iyer",
            dateOfBirth = "21 Sep 2004",
            gender = "Female",
            mobile = "9876512345",
            address = "8, Hill View, Puri",
            aadhaarNumber = "XXXX-XXXX-9012",
            enrollments = listOf(
                CourseEnrollment("ENR-0004", "Fine Art Foundations", "Due", "05 Apr 2026", fees = 9000)
            )
        )
    )

    val courses = listOf(
        Course("CRS-01", "Bridal Mehendi Masterclass", "12 weeks", "Advanced", fees = 15000),
        Course("CRS-02", "Arabic Mehendi Basics", "6 weeks", "Beginner", fees = 6000),
        Course("CRS-03", "Fine Art Foundations", "10 weeks", "Intermediate", fees = 9000)
    )
}
