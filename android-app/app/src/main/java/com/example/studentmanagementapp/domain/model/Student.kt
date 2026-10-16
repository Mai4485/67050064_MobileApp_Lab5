package com.example.studentmanagementapp.domain.model

data class Student(
    val id: Int,
    val serverId: Int?,
    val studentCode: String,
    val name: String,
    val major: String,
    val syncStatus: String
)
