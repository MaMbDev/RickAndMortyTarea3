package dam.pmdm.rickandmortytarea3.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dam.pmdm.rickandmortytarea3.data.model.EpisodeResponse
import dam.pmdm.rickandmortytarea3.data.remote.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EpisodeRepository(private val context: Context) {

    companion object {
        private const val TAG = "EpisodeRepository"
        private const val PREFS_NAME = "RickAndMortyPrefs"
        private const val KEY_SEEN_EPISODES = "seen_episodes"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun getEpisodes(page: Int = 1): EpisodeResponse {
        Log.d(TAG, "Obteniendo episodios, página: $page")
        return withContext(Dispatchers.IO) {
            RetrofitInstance.api.getEpisodes(page)
        }
    }

    suspend fun getSeenEpisodes(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val json = prefs.getString(KEY_SEEN_EPISODES, "[]") ?: "[]"
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    suspend fun markAsSeen(userId: String, episodeId: Int) {
        withContext(Dispatchers.IO) {
            val seenEpisodes = getSeenEpisodes(userId).toMutableList()
            val episodeIdStr = episodeId.toString()

            if (!seenEpisodes.contains(episodeIdStr)) {
                seenEpisodes.add(episodeIdStr)
                saveSeenEpisodes(seenEpisodes)
                Log.d(TAG, "Episodio $episodeId marcado como visto. Total: ${seenEpisodes.size}")
            }
        }
    }

    suspend fun markAsUnseen(userId: String, episodeId: Int) {
        withContext(Dispatchers.IO) {
            val seenEpisodes = getSeenEpisodes(userId).toMutableList()
            val removed = seenEpisodes.remove(episodeId.toString())

            if (removed) {
                saveSeenEpisodes(seenEpisodes)
                Log.d(TAG, "Episodio $episodeId marcado como no visto. Total: ${seenEpisodes.size}")
            }
        }
    }

    private fun saveSeenEpisodes(episodes: List<String>) {
        val json = gson.toJson(episodes)
        prefs.edit().putString(KEY_SEEN_EPISODES, json).apply()
    }
}