package com.example.studentmanagementapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanagementapp.data.repository.StudentRepository
import com.example.studentmanagementapp.model.Student
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    val students: StateFlow<List<Student>> = repository.observeStudents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isOnline by mutableStateOf(true)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun loadStudents() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.refreshFromServer()
                isOnline = true
            } catch (e: Exception) {
                isOnline = false
                errorMessage = "ไม่สามารถเชื่อมต่อ Server ได้ กำลังแสดงข้อมูลที่บันทึกไว้ในเครื่อง"
            } finally {
                isLoading = false
            }
        }
    }

    fun syncStudents() {
        viewModelScope.launch {
            repository.syncPending()
        }
    }

    fun addStudent(studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            repository.addStudent(studentCode, name, major)
        }
    }

    fun updateStudent(original: Student, studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            repository.updateStudent(
                original.copy(studentCode = studentCode, name = name, major = major)
            )
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun filterStudents(students: List<Student>): List<Student> {
        return if (searchQuery.isBlank()) {
            students
        } else {
            students.filter {
                it.studentCode.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.major.contains(searchQuery, ignoreCase = true)
            }
        }
    }
}
