package com.example.laostack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    // 언어 설정을 위한 변수 생성
    private lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 인스턴스 생성 후 마지막 저장된 언어 적용
        languageManager = LanguageManager(this)
        languageManager.applyLastSavedLanguage()
        super.onCreate(savedInstanceState)
        // Splash Screen 생성
        installSplashScreen()

        setContent {
            // LanguageManager을 하위 모든 Compose에 적용
            ProvideLanguageManager(languageManager) {
                NoStack()
            }
        }
    }
}

@Composable
fun NoStack() {
    Navigation()
}
