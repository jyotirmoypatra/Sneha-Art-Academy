package com.shena.snehasacademy.data.models

data class StudentDto(
    val id: String,
    val name: String,
    val courseName: String,
    val status: String
)

data class CourseDto(
    val id: String,
    val title: String,
    val duration: String,
    val level: String
)
