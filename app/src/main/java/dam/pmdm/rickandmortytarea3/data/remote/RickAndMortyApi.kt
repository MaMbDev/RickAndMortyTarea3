package dam.pmdm.rickandmortytarea3.data.remote

import dam.pmdm.rickandmortytarea3.data.model.EpisodeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {

    @GET("episode")
    fun getEpisodes(
        @Query("page") page: Int = 1
    ): Call<EpisodeResponse>
}
