package com.shena.snehasacademy.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.shena.snehasacademy.presentation.attendance.AttendanceScreen
import com.shena.snehasacademy.presentation.auth.AdminLoginScreen
import com.shena.snehasacademy.presentation.auth.AdminRegisterScreen
import com.shena.snehasacademy.presentation.auth.LoginSelectionScreen
import com.shena.snehasacademy.presentation.auth.StudentLoginScreen
import com.shena.snehasacademy.presentation.certificates.CertificateScreen
import com.shena.snehasacademy.presentation.courses.AddCourseScreen
import com.shena.snehasacademy.presentation.courses.CourseDetailsScreen
import com.shena.snehasacademy.presentation.courses.CourseListScreen
import com.shena.snehasacademy.presentation.dashboard.AdminDashboardScreen
import com.shena.snehasacademy.presentation.dashboard.StudentDashboardScreen
import com.shena.snehasacademy.presentation.enrollments.CreateEnrollmentScreen
import com.shena.snehasacademy.presentation.enrollments.EnrollmentDetailsScreen
import com.shena.snehasacademy.presentation.enrollments.EnrollmentScreen
import com.shena.snehasacademy.presentation.fees.FeeManagementScreen
import com.shena.snehasacademy.presentation.fees.PaymentHistoryScreen
import com.shena.snehasacademy.presentation.fees.StudentFeesScreen
import com.shena.snehasacademy.presentation.students.AddStudentScreen
import com.shena.snehasacademy.presentation.students.StudentCoursesScreen
import com.shena.snehasacademy.presentation.students.StudentDetailsScreen
import com.shena.snehasacademy.presentation.students.StudentListScreen
import com.shena.snehasacademy.presentation.students.StudentProfileScreen

@Composable
fun SnehasNavHost() {
    val navController = rememberNavController()
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Route.AdminDashboard.path
    } else {
        Route.LoginSelection.path
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Route.LoginSelection.path) {
            LoginSelectionScreen(
                onAdminLogin = { navController.navigate(Route.AdminLogin.path) },
                onStudentLogin = { navController.navigate(Route.StudentLogin.path) }
            )
        }
        composable(Route.AdminLogin.path) {
            AdminLoginScreen(
                viewModel = viewModel(),
                onLogin = { navController.navigate(Route.AdminDashboard.path) },
                onRegister = { navController.navigate(Route.AdminRegister.path) },
                onBack = { navController.popBackStack(Route.LoginSelection.path, inclusive = false) }
            )
        }
        composable(Route.AdminRegister.path) {
            AdminRegisterScreen(
                viewModel = viewModel(),
                onCreate = { navController.popBackStack(Route.AdminLogin.path, inclusive = false) },
                onBack = { navController.popBackStack(Route.AdminLogin.path, inclusive = false) }
            )
        }
        composable(Route.StudentLogin.path) {
            StudentLoginScreen(
                viewModel = viewModel(),
                onLogin = { studentId -> navController.navigate(Route.StudentDashboard.createRoute(studentId)) },
                onBack = { navController.popBackStack(Route.LoginSelection.path, inclusive = false) }
            )
        }
        composable(Route.AdminDashboard.path) {
            AdminDashboardScreen(
                viewModel = viewModel(),
                onNavigate = { navController.navigate(it.path) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Route.LoginSelection.path) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(
            Route.StudentDashboard.path,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId").orEmpty()
            StudentDashboardScreen(
                studentId = studentId,
                viewModel = viewModel(),
                onNavigate = { route ->
                    when (route) {
                        Route.Settings -> navController.navigate(Route.Settings.createRoute(studentId))
                        Route.StudentCourses -> navController.navigate(Route.StudentCourses.createRoute(studentId))
                        Route.StudentFees -> navController.navigate(Route.StudentFees.createRoute(studentId))
                        else -> navController.navigate(route.path)
                    }
                },
                onLogout = {
                    navController.navigate(Route.LoginSelection.path) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Route.Students.path) {
            StudentListScreen(
                viewModel = viewModel(),
                onAdd = { navController.navigate(Route.AddStudent.path) },
                onOpenDetails = { studentId -> navController.navigate(Route.StudentDetails.createRoute(studentId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.AddStudent.path) {
            AddStudentScreen(viewModel = viewModel(), onBack = { navController.popBackStack() })
        }
        composable(
            Route.StudentDetails.path,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            StudentDetailsScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                viewModel = viewModel(),
                onOpenEnrollment = { studentId, enrollmentId ->
                    navController.navigate(Route.EnrollmentDetails.createRoute(studentId, enrollmentId))
                },
                onEnrollNewCourse = { navController.navigate(Route.CreateEnrollment.path) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Courses.path) {
            CourseListScreen(
                viewModel = viewModel(),
                onAdd = { navController.navigate(Route.AddCourse.path) },
                onOpenDetails = { courseId -> navController.navigate(Route.CourseDetails.createRoute(courseId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.AddCourse.path) {
            AddCourseScreen(viewModel = viewModel(), onBack = { navController.popBackStack() })
        }
        composable(
            Route.StudentCourses.path,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            StudentCoursesScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                viewModel = viewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Route.CourseDetails.path,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            CourseDetailsScreen(
                courseId = backStackEntry.arguments?.getString("courseId").orEmpty(),
                viewModel = viewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Enrollments.path) {
            EnrollmentScreen(
                viewModel = viewModel(),
                onCreateEnrollment = { navController.navigate(Route.CreateEnrollment.path) },
                onOpenDetails = { studentId, enrollmentId ->
                    navController.navigate(Route.EnrollmentDetails.createRoute(studentId, enrollmentId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.CreateEnrollment.path) {
            CreateEnrollmentScreen(viewModel = viewModel(), onBack = { navController.popBackStack() })
        }
        composable(
            Route.EnrollmentDetails.path,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("enrollmentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            EnrollmentDetailsScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                enrollmentId = backStackEntry.arguments?.getString("enrollmentId").orEmpty(),
                viewModel = viewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Attendance.path) { AttendanceScreen { navController.popBackStack() } }
        composable(Route.Fees.path) {
            FeeManagementScreen(
                viewModel = viewModel(),
                onOpenHistory = { studentId, enrollmentId ->
                    navController.navigate(Route.PaymentHistory.createRoute(studentId, enrollmentId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Route.PaymentHistory.path,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("enrollmentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            PaymentHistoryScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                enrollmentId = backStackEntry.arguments?.getString("enrollmentId").orEmpty(),
                viewModel = viewModel(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Route.StudentFees.path,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getString("studentId").orEmpty()
            StudentFeesScreen(
                studentId = studentId,
                viewModel = viewModel(),
                onOpenHistory = { id, enrollmentId ->
                    navController.navigate(Route.StudentPaymentHistory.createRoute(id, enrollmentId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Route.StudentPaymentHistory.path,
            arguments = listOf(
                navArgument("studentId") { type = NavType.StringType },
                navArgument("enrollmentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            PaymentHistoryScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                enrollmentId = backStackEntry.arguments?.getString("enrollmentId").orEmpty(),
                viewModel = viewModel(),
                readOnly = true,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Route.Certificates.path) {
            CertificateScreen(viewModel = viewModel(), onBack = { navController.popBackStack() })
        }
        composable(
            Route.Settings.path,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            StudentProfileScreen(
                studentId = backStackEntry.arguments?.getString("studentId").orEmpty(),
                viewModel = viewModel(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
