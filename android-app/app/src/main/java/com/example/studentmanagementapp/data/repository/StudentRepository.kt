package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.StudentRequest
import com.example.studentmanagementapp.model.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {
    fun observeStudents(): Flow<List<Student>> {
        return dao.observeStudents()
    }

    suspend fun getLocalStudents(): List<Student> {
        return dao.getAllStudents()
    }

    suspend fun refreshFromServer() {
        val remoteStudents = api.getStudents()
        val entities = remoteStudents.map { dto ->
            Student(
                serverId = dto.id,
                studentCode = dto.studentCode,
                name = dto.name,
                major = dto.major,
                syncStatus = "SYNCED"
            )
        }
        dao.deleteSynced()
        dao.insertAll(entities)
    }

    suspend fun addStudent(studentCode: String, name: String, major: String) {
        dao.insertStudent(
            Student(
                studentCode = studentCode,
                name = name,
                major = major,
                syncStatus = "PENDING"
            )
        )
    }

    suspend fun syncPending() {
        val pending = dao.getPendingStudents()
        for (student in pending) {
            try {
                val response = api.createStudent(
                    StudentRequest(
                        studentCode = student.studentCode,
                        name = student.name,
                        major = student.major
                    )
                )
                dao.updateStudent(
                    student.copy(
                        serverId = response.id,
                        syncStatus = "SYNCED"
                    )
                )
            } catch (e: Exception) {
                dao.updateStudent(student.copy(syncStatus = "FAILED"))
            }
        }
    }

    suspend fun updateStudent(student: Student) {
        val serverId = student.serverId
        try {
            if (serverId != null) {
                api.updateStudent(
                    serverId,
                    StudentRequest(
                        studentCode = student.studentCode,
                        name = student.name,
                        major = student.major
                    )
                )
                dao.updateStudent(student.copy(syncStatus = "SYNCED"))
            } else {
                dao.updateStudent(student.copy(syncStatus = "PENDING"))
            }
        } catch (e: Exception) {
            dao.updateStudent(student.copy(syncStatus = "FAILED"))
        }
    }

    suspend fun deleteStudent(student: Student) {
        try {
            val serverId = student.serverId
            if (serverId != null) {
                api.deleteStudent(serverId)
            }
            dao.deleteStudent(student)
        } catch (e: Exception) {
            dao.deleteStudent(student)
        }
    }
}
