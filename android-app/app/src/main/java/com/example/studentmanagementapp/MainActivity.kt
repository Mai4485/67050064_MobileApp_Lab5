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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.studentmanagementapp.data.local.AppDatabase
import com.example.studentmanagementapp.data.remote.RetrofitClient
import com.example.studentmanagementapp.data.remote.TokenStorage
import com.example.studentmanagementapp.data.repository.StudentRepository
import com.example.studentmanagementapp.ui.AddEditStudentScreen
import com.example.studentmanagementapp.ui.StudentDetailScreen
import com.example.studentmanagementapp.ui.StudentListScreen
import com.example.studentmanagementapp.ui.login.LoginScreen
import com.example.studentmanagementapp.ui.login.LoginViewModel
import com.example.studentmanagementapp.ui.login.LoginViewModelFactory
import com.example.studentmanagementapp.ui.theme.StudentManagementAppTheme
import com.example.studentmanagementapp.viewmodel.StudentViewModel
import com.example.studentmanagementapp.viewmodel.StudentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(applicationContext)
        val authApi = RetrofitClient.authApi(tokenStorage)
        val studentApi = RetrofitClient.studentApi(tokenStorage)

        val dao = AppDatabase.getDatabase(applicationContext).studentDao()
        val repository = StudentRepository(dao, studentApi)
        val studentFactory = StudentViewModelFactory(repository)
        val loginFactory = LoginViewModelFactory(authApi, tokenStorage)

        setContent {
            StudentManagementAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: StudentViewModel = viewModel(factory = studentFactory)
                    StudentManagementApp(
                        viewModel = viewModel,
                        loginFactory = loginFactory,
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
    loginFactory: LoginViewModelFactory,
    tokenStorage: TokenStorage
) {
    val navController = rememberNavController()
    val startDestination = if (tokenStorage.getToken() != null) "list" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(factory = loginFactory)
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
