package com.example.studentmanagementapp.data.local

import androidx.room.*
import com.example.studentmanagementapp.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<Student>

    @Query("SELECT * FROM students ORDER BY name")
    fun observeStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE syncStatus = 'PENDING'")
    suspend fun getPendingStudents(): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    //ดึงข้อมูลจาก API มาเก็บ cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    //ลบข้อมูลทั้งหมดในตาราง
    @Query("DELETE FROM students")
    suspend fun deleteAll()

    @Query("DELETE FROM students WHERE syncStatus = 'SYNCED'")
    suspend fun deleteSynced()
}
