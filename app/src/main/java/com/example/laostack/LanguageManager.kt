package com.example.laostack

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

class LanguageManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)

    fun updateLanguage(languageCode: String, recreateActivity: Boolean = true) {
        if (getSavedLanguage() != languageCode) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)

            // Save the selected language
            saveLanguagePreference(languageCode)

            // Activity 재시작 (필요한 경우에만)
            if (recreateActivity && context is Activity) {
                context.recreate()
            }
        }
    }

    private fun saveLanguagePreference(languageCode: String) {
        sharedPreferences.edit().putString("selected_language", languageCode).apply()
    }

    fun getSavedLanguage(): String {
        return sharedPreferences.getString("selected_language", "ko") ?: "ko"
    }

    fun applyLastSavedLanguage() {
        val savedLanguage = getSavedLanguage()
        updateLanguage(savedLanguage, false)
    }
}

val LocalLanguageManager = staticCompositionLocalOf<LanguageManager> { error("LanguageManager not provided") }

@Composable
fun ProvideLanguageManager(languageManager: LanguageManager, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLanguageManager provides languageManager) {
        content()
    }
}