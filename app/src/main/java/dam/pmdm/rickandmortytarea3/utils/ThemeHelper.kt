package dam.pmdm.rickandmortytarea3.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import dam.pmdm.rickandmortytarea3.data.preferences.AppPreferences
import java.util.*
import dam.pmdm.rickandmortytarea3.R

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
            else -> Locale("es", "ES")
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getCurrentLanguageName(context: Context, language: String): String {
        return when (language) {
            AppPreferences.LANGUAGE_SPANISH -> context.getString(R.string.spanish)
            AppPreferences.LANGUAGE_ENGLISH -> context.getString(R.string.english)
            else -> context.getString(R.string.spanish)
        }
    }

    fun getCurrentThemeName(context: Context, theme: String): String {
        return when (theme) {
            AppPreferences.THEME_LIGHT -> context.getString(R.string.light)
            AppPreferences.THEME_DARK -> context.getString(R.string.dark)
            AppPreferences.THEME_SYSTEM -> context.getString(R.string.system)
            else -> context.getString(R.string.light)
        }
    }
}