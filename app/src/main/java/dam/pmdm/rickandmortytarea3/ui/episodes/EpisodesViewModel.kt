package dam.pmdm.rickandmortytarea3.ui.episodes

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam.pmdm.rickandmortytarea3.data.model.Episode
import dam.pmdm.rickandmortytarea3.data.repository.EpisodeApiRepository
import dam.pmdm.rickandmortytarea3.data.repository.SharedPreferencesRepository
import kotlinx.coroutines.launch

class EpisodesViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "EpisodesViewModel"
    }

    // SharedPreferencesRepository para episodios vistos
    private val sharedPrefsRepo = SharedPreferencesRepository(application.applicationContext)

    // EpisodeApiRepository solo para obtener los episodios de la API
    private val episodeApiRepo = EpisodeApiRepository()

    // LiveData para compartir entre fragments
    private val _episodes = MutableLiveData<List<Episode>>()
    val episodes: LiveData<List<Episode>> = _episodes

    private val _seenEpisodes = MutableLiveData<Set<Int>>()
    val seenEpisodes: LiveData<Set<Int>> = _seenEpisodes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // evita cargar múltiples veces
    private var episodesLoaded = false

    init {
        Log.d(TAG, "ViewModel inicializado")
        loadSeenEpisodes()
    }

    fun loadEpisodesIfNeeded() {
        if (!episodesLoaded) {
            loadEpisodes()
        }
    }

    fun loadEpisodes(page: Int = 1) {
        viewModelScope.launch {
            Log.d(TAG, "Cargando episodios, página: $page")
            _isLoading.value = true
            _error.value = null

            try {
                val response = episodeApiRepo.getEpisodes(page)
                _episodes.value = response.results
                episodesLoaded = true
                Log.d(TAG, "Episodios cargados exitosamente: ${response.results.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error cargando episodios: ${e.message}", e)
                _error.value = "Error cargando episodios: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshEpisodes() {
        episodesLoaded = false
        loadEpisodes()
    }

    private fun loadSeenEpisodes() {
        Log.d(TAG, "Cargando episodios vistos desde SharedPreferences")
        val seen = sharedPrefsRepo.getSeenEpisodes()
        Log.d(TAG, "Episodios vistos cargados: ${seen.size}")
        _seenEpisodes.value = seen
    }

    fun toggleEpisodeSeen(episodeId: Int) {
        Log.d(TAG, "=== toggleEpisodeSeen llamado ===")
        Log.d(TAG, "Episodio ID: $episodeId")

        val currentSeen = _seenEpisodes.value ?: emptySet()

        if (currentSeen.contains(episodeId)) {
            Log.d(TAG, "Marcando como NO visto")
            sharedPrefsRepo.markAsUnseen(episodeId)
            _seenEpisodes.value = currentSeen - episodeId
            Log.d(TAG, "Episodio $episodeId marcado como NO visto")
        } else {
            Log.d(TAG, "Marcando como visto")
            sharedPrefsRepo.markAsSeen(episodeId)
            _seenEpisodes.value = currentSeen + episodeId
            Log.d(TAG, "Episodio $episodeId marcado como visto")
        }
    }

    fun isEpisodeSeen(episodeId: Int): Boolean {
        return _seenEpisodes.value?.contains(episodeId) ?: false
    }

    fun getStats(): Triple<Int, Int, Int> {
        val total = _episodes.value?.size ?: 0
        val seen = _seenEpisodes.value?.size ?: 0
        val percentage = if (total > 0) (seen * 100 / total) else 0

        return Triple(total, seen, percentage)
    }

    // Método para debugging
    fun debugSeenEpisodes() {
        Log.d(TAG, "=== DEBUG Seen Episodes ===")
        val seen = sharedPrefsRepo.getSeenEpisodes()
        Log.d(TAG, "Total: ${seen.size}")
        Log.d(TAG, "Lista: $seen")
    }
}