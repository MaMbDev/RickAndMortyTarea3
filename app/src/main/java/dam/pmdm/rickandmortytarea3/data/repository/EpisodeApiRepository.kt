package dam.pmdm.rickandmortytarea3.data.repository

import android.util.Log
import dam.pmdm.rickandmortytarea3.data.model.EpisodeResponse
import dam.pmdm.rickandmortytarea3.data.remote.RetrofitInstance

class EpisodeApiRepository {

    companion object {
        private const val TAG = "EpisodeApiRepository"
    }

    suspend fun getEpisodes(page: Int = 1): EpisodeResponse {
        Log.d(TAG, "Obteniendo episodios, página: $page")
        return RetrofitInstance.api.getEpisodes(page)
    }
}