package com.example.studentmanagementapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentEntity>

    @Query("SELECT * FROM students ORDER BY name")
    fun observeStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE syncStatus = 'PENDING'")
    suspend fun getPendingStudents(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    //ดึงข้อมูลจาก API มาเก็บ cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    //ลบข้อมูลทั้งหมดในตาราง
    @Query("DELETE FROM students")
    suspend fun deleteAll()

    @Query("DELETE FROM students WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()
}
