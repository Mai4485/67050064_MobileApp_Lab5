package com.example.studentmanagementapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studentmanagementapp.domain.usecase.AddStudentUseCase
import com.example.studentmanagementapp.domain.usecase.DeleteStudentUseCase
import com.example.studentmanagementapp.domain.usecase.GetStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.RefreshStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.SyncStudentsUseCase
import com.example.studentmanagementapp.domain.usecase.UpdateStudentUseCase

class StudentViewModelFactory(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val refreshStudentsUseCase: RefreshStudentsUseCase,
    private val syncStudentsUseCase: SyncStudentsUseCase,
    private val addStudentUseCase: AddStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentViewModel(
            getStudentsUseCase,
            refreshStudentsUseCase,
            syncStudentsUseCase,
            addStudentUseCase,
            updateStudentUseCase,
            deleteStudentUseCase
        ) as T
    }
}
