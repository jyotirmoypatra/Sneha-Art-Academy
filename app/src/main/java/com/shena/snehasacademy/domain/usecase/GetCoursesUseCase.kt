package com.shena.snehasacademy.domain.usecase

import com.shena.snehasacademy.domain.repository.AcademyRepository

class GetCoursesUseCase(
    private val repository: AcademyRepository
) {
    suspend operator fun invoke() = repository.getCourses()
}
