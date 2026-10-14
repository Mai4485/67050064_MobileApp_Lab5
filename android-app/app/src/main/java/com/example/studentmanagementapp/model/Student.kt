package com.example.studentmanagementapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val serverId: Int? = null,
    val studentCode: String,
    val name: String,
    val major: String,
    val syncStatus: String = "PENDING",
    val updatedAt: Long = System.currentTimeMillis()
)
