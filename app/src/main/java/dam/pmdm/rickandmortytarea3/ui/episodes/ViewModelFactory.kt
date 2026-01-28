package dam.pmdm.rickandmortytarea3.ui.episodes

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EpisodesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodesViewModel::class.java)) {
            return EpisodesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}