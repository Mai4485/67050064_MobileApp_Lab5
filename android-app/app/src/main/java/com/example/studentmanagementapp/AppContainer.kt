package com.example.studentmanagementapp

import android.content.Context
import com.example.studentmanagementapp.data.local.AppDatabase
import com.example.studentmanagementapp.data.remote.AuthApi
import com.example.studentmanagementapp.data.remote.RetrofitClient
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.TokenStorage
import com.example.studentmanagementapp.data.repository.StudentRepositoryImpl

class AppContainer(context: Context) {

    val tokenStorage = TokenStorage(context)

    val authApi: AuthApi = RetrofitClient.authApi(tokenStorage)

    val studentApi: StudentApi = RetrofitClient.studentApi(tokenStorage)

    private val dao = AppDatabase.getDatabase(context).studentDao()

    val studentRepository = StudentRepositoryImpl(dao, studentApi)
}
