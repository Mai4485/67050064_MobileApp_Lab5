package com.example.studentmanagementapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanagementapp.domain.model.Student
import com.example.studentmanagementapp.domain.usecase.AddStudentUseCase
import com.example.studentmanagementapp.domain.usecase.DeleteStudentUseCase
import com.example.studentmanagementapp.domain.usecase.GetStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.RefreshStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.SyncStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.UpdateStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val refreshStudentsUseCase: RefreshStudentsUseCase,
    private val syncStudentsUseCase: SyncStudentsUseCase,
    private val addStudentUseCase: AddStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase
) : ViewModel() {

    val students: StateFlow<List<Student>> = getStudentsUseCase()
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
                refreshStudentsUseCase()
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
            try {
                syncStudentsUseCase()
            } catch (e: Exception) {
                errorMessage = "Sync ไม่สำเร็จ ลองใหม่อีกครั้ง"
            }
        }
    }

    fun addStudent(studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            addStudentUseCase(studentCode, name, major)
        }
    }

    fun updateStudent(original: Student, studentCode: String, name: String, major: String) {
        viewModelScope.launch {
            updateStudentUseCase(
                original.copy(studentCode = studentCode, name = name, major = major)
            )
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            deleteStudentUseCase(student)
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
