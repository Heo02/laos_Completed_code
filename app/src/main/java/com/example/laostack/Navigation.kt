package com.example.laostack

import android.app.AlertDialog
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.laostack.view.home.HomeView
import com.example.laostack.view.home.SelectMethodView
import com.example.laostack.view.home.UploadView

@Composable
fun Navigation() {
    val context = LocalContext.current

    // 이동한 화면을 스택으로 관리
    val navController = rememberNavController()
    // context를 ComponentActivity로 캐스팅하여 Activity 레벨의 작업이 가능하게 해줌
    val activity = context as ComponentActivity
    val startDestination by remember { mutableStateOf("main") }

    NavHost(navController = navController, startDestination = startDestination) {

        // 메인 홈 화면
        composable("main") { HomeView(navController) }

        // 방식 선택 화면
        composable("select_method") { SelectMethodView(navController) }

        // 파일 업로드 화면
        composable("upload_file") { UploadView(navController) }
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