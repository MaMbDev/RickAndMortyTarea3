package dam.pmdm.rickandmortytarea3.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import dam.pmdm.rickandmortytarea3.data.preferences.AppPreferences
import java.util.*

object ThemeHelper {

    fun applyTheme(theme: String) {
        when (theme) {
            AppPreferences.THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            AppPreferences.THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            AppPreferences.THEME_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun applyLanguage(context: Context, language: String) {
        val locale = when (language) {
            AppPreferences.LANGUAGE_ENGLISH -> Locale.ENGLISH
            else -> Locale("es", "ES")  // Español
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun restartActivity(activity: Activity) {
        activity.recreate()
    }

    fun getCurrentLanguageName(language: String): String {
        return when (language) {
            AppPreferences.LANGUAGE_SPANISH -> "Español"
            AppPreferences.LANGUAGE_ENGLISH -> "English"
            else -> "Español"
        }
    }

    fun getCurrentThemeName(theme: String): String {
        return when (theme) {
            AppPreferences.THEME_LIGHT -> "Claro"
            AppPreferences.THEME_DARK -> "Oscuro"
            AppPreferences.THEME_SYSTEM -> "Sistema"
            else -> "Claro"
        }
    }
}