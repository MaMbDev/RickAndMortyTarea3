package dam.pmdm.rickandmortytarea3.data.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesRepository(private val context: Context) {

    companion object {
        private const val TAG = "SharedPrefsRepo"
        private const val PREFS_NAME = "RickAndMortyPrefs"
        private const val KEY_SEEN_EPISODES = "seen_episodes"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSeenEpisodes(): Set<Int> {
        val json = prefs.getString(KEY_SEEN_EPISODES, "[]") ?: "[]"
        val type = object : TypeToken<Set<Int>>() {}.type
        val episodes = gson.fromJson<Set<Int>>(json, type) ?: emptySet()
        Log.d(TAG, "Episodios vistos obtenidos: ${episodes.size}")
        return episodes
    }

    fun markAsSeen(episodeId: Int) {
        val seenEpisodes = getSeenEpisodes().toMutableSet()
        seenEpisodes.add(episodeId)
        saveSeenEpisodes(seenEpisodes)
        Log.d(TAG, "Episodio $episodeId marcado como visto. Total: ${seenEpisodes.size}")
    }

    fun markAsUnseen(episodeId: Int) {
        val seenEpisodes = getSeenEpisodes().toMutableSet()
        seenEpisodes.remove(episodeId)
        saveSeenEpisodes(seenEpisodes)
        Log.d(TAG, "Episodio $episodeId marcado como no visto. Total: ${seenEpisodes.size}")
    }

    private fun saveSeenEpisodes(episodes: Set<Int>) {
        val json = gson.toJson(episodes)
        prefs.edit().putString(KEY_SEEN_EPISODES, json).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
        Log.d(TAG, "Datos de SharedPreferences eliminados")
    }
}