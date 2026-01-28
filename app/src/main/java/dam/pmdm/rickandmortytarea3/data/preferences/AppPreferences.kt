package dam.pmdm.rickandmortytarea3.data.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"
        private const val DEFAULT_LANGUAGE = "es"
        private const val DEFAULT_THEME = "light"

        const val LANGUAGE_SPANISH = "es"
        const val LANGUAGE_ENGLISH = "en"

        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var theme: String
        get() = prefs.getString(KEY_THEME, DEFAULT_THEME) ?: DEFAULT_THEME
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}