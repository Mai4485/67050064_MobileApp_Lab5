package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.repository.StudentRepository
import javax.inject.Inject

class AddStudentUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    suspend operator fun invoke(studentCode: String, name: String, major: String) {
        repository.addStudent(studentCode, name, major)
    }
}
