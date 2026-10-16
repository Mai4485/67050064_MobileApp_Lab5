package com.example.studentmanagementapp.data.repository

import com.example.studentmanagementapp.data.local.StudentDao
import com.example.studentmanagementapp.data.local.StudentEntity
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.StudentRequest
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val dao: StudentDao,
    private val api: StudentApi
) : StudentRepository {

    override fun observeStudents(): Flow<List<Student>> {
        return dao.observeStudents().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshFromServer() {
        val remoteStudents = api.getStudents()
        val entities = remoteStudents.map { dto ->
            StudentEntity(
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

    override suspend fun addStudent(studentCode: String, name: String, major: String) {
        dao.insertStudent(
            StudentEntity(studentCode = studentCode, name = name, major = major, syncStatus = "PENDING")
        )
    }

    override suspend fun syncPending() {
        val pending = dao.getPendingStudents()
        for (student in pending) {
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
        }
    }

    override suspend fun updateStudent(student: Student) {
        val entity = student.toEntity()
        val serverId = entity.serverId
        try {
            if (serverId != null) {
                api.updateStudent(
                    serverId,
                    StudentRequest(studentCode = entity.studentCode, name = entity.name, major = entity.major)
                )
                dao.updateStudent(entity.copy(syncStatus = "SYNCED"))
            } else {
                dao.updateStudent(entity.copy(syncStatus = "PENDING"))
            }
        } catch (e: Exception) {
            dao.updateStudent(entity.copy(syncStatus = "FAILED"))
        }
    }

    override suspend fun deleteStudent(student: Student) {
        val entity = student.toEntity()
        try {
            val serverId = entity.serverId
            if (serverId != null) {
                api.deleteStudent(serverId)
            }
            dao.deleteStudent(entity)
        } catch (e: Exception) {
            dao.deleteStudent(entity)
        }
    }

    private fun StudentEntity.toDomain(): Student {
        return Student(
            id = id,
            serverId = serverId,
            studentCode = studentCode,
            name = name,
            major = major,
            syncStatus = syncStatus
        )
    }

    private fun Student.toEntity(): StudentEntity {
        return StudentEntity(
            id = id,
            serverId = serverId,
            studentCode = studentCode,
            name = name,
            major = major,
            syncStatus = syncStatus
        )
    }
}
