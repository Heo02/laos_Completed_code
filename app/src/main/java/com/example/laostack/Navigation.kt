package com.example.laostack

import android.app.AlertDialog
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.laostack.api.APIRetrofit.apiService
import com.example.laostack.view.home.CreateView
import com.example.laostack.view.home.CreatingView
import com.example.laostack.view.home.HomeView
import com.example.laostack.view.home.SelectMethodView
import com.example.laostack.view.home.UploadView
import com.example.laostack.view.meaning.MeaningView
import kotlinx.serialization.json.Json

@Composable
fun Navigation() {
    val context = LocalContext.current

    // 이동한 화면을 스택으로 관리
    val navController = rememberNavController()
    // context를 ComponentActivity로 캐스팅하여 Activity 레벨의 작업이 가능하게 해줌
    val activity = context as ComponentActivity
    val startDestination by remember { mutableStateOf("main") }

    NavHost(navController = navController, startDestination = startDestination) {

        // Home Screen
        composable("main") { HomeView(navController) }

        // SelectMethod Screen
        composable("select_method") { SelectMethodView(navController) }

        // Upload Screen
        composable("upload_file") { UploadView(navController) }

        // Creating Screen
        composable(
            route = "Creating/{category}/{imageUri}",
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("imageUri") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val categoryParam = backStackEntry.arguments?.getString("category")
            val imageUriString = backStackEntry.arguments?.getString("imageUri")

            val category = if (categoryParam != "null") categoryParam else null
            val imageUri = imageUriString?.let {
                if (it != "null") Uri.parse(it) else null
            }
            CreatingView(navController, apiService, category, imageUri)
        }

        // Create Screen
        composable(
            route = "Create/{message}/{createToOCR}",
            arguments = listOf(
                navArgument("message") { type = NavType.StringType },
                navArgument("createToOCR") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val messageJson = backStackEntry.arguments?.getString("message")
            val createToOCR = backStackEntry.arguments?.getBoolean("createToOCR") ?: false
            val message = messageJson?.let {
                Json.decodeFromString<CreateTextMessage>(it)
            }
            CreateView(navController, apiService, message, createToOCR)
        }

        // Meaning Screen
        composable("meaning") { MeaningView(navController, apiService) }
    }



    // 뒤로가기 버튼 동작시 완전 종료 여부 묻기
    BackHandler {
        if (navController.previousBackStackEntry == null) {
            showExitDialog(activity)
        } else {
            navController.popBackStack()
        }
    }
}

// 앱 종료 여부 묻는 다이얼로그
fun showExitDialog(activity: ComponentActivity) {
    AlertDialog.Builder(activity)
        .setTitle(activity.getString(R.string.exit_app_title))
        .setMessage(activity.getString(R.string.exit_app_message))
        .setPositiveButton(activity.getString(R.string.exit_yes)) { _, _ -> activity.finish() }
        .setNegativeButton(activity.getString(R.string.exit_no), null)
        .show()
}