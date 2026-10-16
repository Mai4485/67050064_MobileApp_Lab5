package com.example.studentmanagementapp.domain.usecase

import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeStudentRepository : StudentRepository {

    private val students = MutableStateFlow(
        listOf(
            Student(
                id = 1,
                serverId = 1,
                studentCode = "66001",
                name = "สมชาย",
                major = "IT",
                syncStatus = "SYNCED"
            )
        )
    )

    override fun observeStudents(): Flow<List<Student>> {
        return students
    }

    override suspend fun refreshFromServer() {
    }

    override suspend fun addStudent(studentCode: String, name: String, major: String) {
        students.value = students.value + Student(
            id = students.value.size + 1,
            serverId = null,
            studentCode = studentCode,
            name = name,
            major = major,
            syncStatus = "PENDING"
        )
    }

    override suspend fun updateStudent(student: Student) {
        students.value = students.value.map { if (it.id == student.id) student else it }
    }

    override suspend fun deleteStudent(student: Student) {
        students.value = students.value.filter { it.id != student.id }
    }

    override suspend fun syncPending() {
    }
}

class GetStudentsUseCaseTest {

    @Test
    fun `get students returns student list`() = runTest {
        val repository = FakeStudentRepository()
        val useCase = GetStudentsUseCase(repository)

        val result = useCase().first()

        assertEquals(1, result.size)
        assertEquals("สมชาย", result.first().name)
    }
}
