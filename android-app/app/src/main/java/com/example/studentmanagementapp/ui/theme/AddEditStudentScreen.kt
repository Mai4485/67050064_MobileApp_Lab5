package com.example.studentmanagementapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanagementapp.domain.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditStudentScreen(
    existingStudent: Student? = null,
    onSave: (studentCode: String, name: String, major: String) -> Unit,
    onCancel: () -> Unit
) {
    var studentCode by remember { mutableStateOf(existingStudent?.studentCode ?: "") }
    var name by remember { mutableStateOf(existingStudent?.name ?: "") }
    var major by remember { mutableStateOf(existingStudent?.major ?: "") }

    val isEditMode = existingStudent != null

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (isEditMode) "Edit Student" else "Add Student") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = studentCode,
                onValueChange = { studentCode = it },
                label = { Text("Student Code") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = major,
                onValueChange = { major = it },
                label = { Text("Major") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (studentCode.isNotBlank() && name.isNotBlank() && major.isNotBlank()) {
                        onSave(studentCode, name, major)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "UPDATE" else "SAVE")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        }
    }
}
