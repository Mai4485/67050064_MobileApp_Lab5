package com.example.studentmanagementapp.di

import android.content.Context
import com.example.studentmanagementapp.data.remote.AuthApi
import com.example.studentmanagementapp.data.remote.RetrofitClient
import com.example.studentmanagementapp.data.remote.StudentApi
import com.example.studentmanagementapp.data.remote.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return TokenStorage(context)
    }

    @Provides
    fun provideStudentApi(tokenStorage: TokenStorage): StudentApi {
        return RetrofitClient.studentApi(tokenStorage)
    }

    @Provides
    fun provideAuthApi(tokenStorage: TokenStorage): AuthApi {
        return RetrofitClient.authApi(tokenStorage)
    }
}
