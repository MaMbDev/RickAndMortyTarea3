package dam.pmdm.rickandmortytarea3

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.rickandmortytarea3.data.preferences.AppPreferences
import dam.pmdm.rickandmortytarea3.utils.LanguageHelper
import dam.pmdm.rickandmortytarea3.utils.ThemeHelper
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val appPreferences = AppPreferences(this)
        ThemeHelper.applyTheme(appPreferences.theme)

        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        val appPreferences = AppPreferences(newBase)
        super.attachBaseContext(LanguageHelper.getLocalizedContext(newBase, appPreferences.language))
    }

    override fun onResume() {
        super.onResume()

        checkLanguageChange()
    }

    private fun checkLanguageChange() {
        val appPreferences = AppPreferences(this)
        val currentLang = appPreferences.language


        val configLang = resources.configuration.locale.language
        val savedLang = when (currentLang) {
            "en" -> "en"
            else -> "es"
        }

        if (savedLang != configLang) {

            recreate()
        }
    }
}