package com.example.studentmanagementapp.domain.repository

import com.example.studentmanagementapp.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun observeStudents(): Flow<List<Student>>
    suspend fun refreshFromServer()
    suspend fun addStudent(studentCode: String, name: String, major: String)
    suspend fun updateStudent(student: Student)
    suspend fun deleteStudent(student: Student)
    suspend fun syncPending()
}
