package com.example.studentmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studentmanagementapp.data.remote.TokenStorage
import com.example.studentmanagementapp.ui.AddEditStudentScreen
import com.example.studentmanagementapp.ui.StudentDetailScreen
import com.example.studentmanagementapp.ui.StudentListScreen
import com.example.studentmanagementapp.ui.login.LoginScreen
import com.example.studentmanagementapp.ui.login.LoginViewModel
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme
import com.example.studentmanagementapp.viewmodel.StudentViewModel
import com.example.studentmanagementapp.worker.SyncScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SyncScheduler.schedulePeriodicSync(this)

        setContent {
            StudentManagementAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: StudentViewModel = hiltViewModel()
                    StudentManagementApp(
                        viewModel = viewModel,
                        tokenStorage = tokenStorage
                    )
                }
            }
        }
    }
}

@Composable
fun StudentManagementApp(
    viewModel: StudentViewModel,
    tokenStorage: TokenStorage
) {
    val navController = rememberNavController()
    val startDestination = if (tokenStorage.getToken() != null) "list" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("list") {
            StudentListScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add") },
                onStudentClick = { student -> navController.navigate("detail/${student.id}") },
                onLogout = {
                    tokenStorage.clearToken()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("add") {
            AddEditStudentScreen(
                onSave = { code, name, major ->
                    viewModel.addStudent(code, name, major)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "detail/{studentId}",
            arguments = listOf(navArgument("studentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1
            val students by viewModel.students.collectAsState()
            val student = students.find { it.id == studentId }

            if (student != null) {
                StudentDetailScreen(
                    student = student,
                    onEdit = { navController.navigate("edit/${student.id}") },
                    onDelete = {
                        viewModel.deleteStudent(student)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "edit/{studentId}",
            arguments = listOf(navArgument("studentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1
            val students by viewModel.students.collectAsState()
            val student = students.find { it.id == studentId }

            if (student != null) {
                AddEditStudentScreen(
                    existingStudent = student,
                    onSave = { code, name, major ->
                        viewModel.updateStudent(student, code, name, major)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
