package dam.pmdm.rickandmortytarea3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import dam.pmdm.rickandmortytarea3.data.preferences.AppPreferences
import dam.pmdm.rickandmortytarea3.databinding.FragmentSettingsBinding
import dam.pmdm.rickandmortytarea3.utils.LanguageHelper
import dam.pmdm.rickandmortytarea3.utils.ThemeHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())

        setupLanguageSpinner()
        setupThemeSpinner()
        setupLogoutButton()
        loadCurrentSettings()
    }

    private fun setupLanguageSpinner() {
        // Crear lista de idiomas
        val languages = arrayOf("Español", "English")
        val languageAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages
        )
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = languageAdapter

        // Carga idioma actual
        val currentLanguage = appPreferences.language
        val position = when (currentLanguage) {
            AppPreferences.LANGUAGE_ENGLISH -> 1  // English
            else -> 0  // Español
        }
        binding.spinnerLanguage.setSelection(position)

        // Listener para cambios
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newLanguage = when (position) {
                    1 -> AppPreferences.LANGUAGE_ENGLISH
                    else -> AppPreferences.LANGUAGE_SPANISH
                }

                if (newLanguage != appPreferences.language) {
                    appPreferences.language = newLanguage
                    Toast.makeText(
                        requireContext(),
                        "Idioma cambiado a: ${ThemeHelper.getCurrentLanguageName(requireContext(), newLanguage)}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reinicia actividad para aplicar cambios
                    restartApp()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    private fun setupThemeSpinner() {
        // Crea lista de temas
        val themes = arrayOf("Claro", "Oscuro", "Sistema")
        val themeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            themes
        )
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTheme.adapter = themeAdapter

        // Carga tema actual
        val currentTheme = appPreferences.theme
        val position = when (currentTheme) {
            AppPreferences.THEME_DARK -> 1
            AppPreferences.THEME_SYSTEM -> 2
            else -> 0  // Claro
        }
        binding.spinnerTheme.setSelection(position)

        // Listener para cambios
        binding.spinnerTheme.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newTheme = when (position) {
                    1 -> AppPreferences.THEME_DARK
                    2 -> AppPreferences.THEME_SYSTEM
                    else -> AppPreferences.THEME_LIGHT
                }

                if (newTheme != appPreferences.theme) {
                    appPreferences.theme = newTheme
                    Toast.makeText(
                        requireContext(),
                        "Tema cambiado a: ${ThemeHelper.getCurrentThemeName(requireContext(), newTheme)}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Reinicia actividad para aplicar cambios
                    restartApp()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadCurrentSettings() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: getString(R.string.user)

        binding.tvCurrentSettings.text = """
      ${getString(R.string.current_settings)}:
    • ${getString(R.string.language)}: ${ThemeHelper.getCurrentLanguageName(requireContext(), appPreferences.language)}
    • ${getString(R.string.theme)}: ${ThemeHelper.getCurrentThemeName(requireContext(), appPreferences.theme)}
    • ${getString(R.string.user)}: $userEmail
""".trimIndent()
    }
    private fun restartApp() {
        //  cambio de idioma inmediatamente
        val appPreferences = AppPreferences(requireContext())
        LanguageHelper.applyLanguage(requireContext(), appPreferences.language)

        // Reinicia la MainActivity para aplicar cambios
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()

        //  recreación de la actividad actual
        requireActivity().recreate()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
        Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}