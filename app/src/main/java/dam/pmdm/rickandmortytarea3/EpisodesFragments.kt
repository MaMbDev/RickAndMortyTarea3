package dam.pmdm.rickandmortytarea3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.rickandmortytarea3.data.model.Episode
import dam.pmdm.rickandmortytarea3.data.model.EpisodeResponse
import dam.pmdm.rickandmortytarea3.data.remote.RetrofitInstance
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EpisodesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAll: Button
    private lateinit var btnSeen: Button
    private lateinit var adapter: EpisodesAdapter

    private val seenIds = mutableSetOf<Int>()
    private var allEpisodes: List<Episode> = emptyList()
    private var showingSeenOnly = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_episodes, container, false)

        recyclerView = view.findViewById(R.id.rvEpisodes)
        btnAll = view.findViewById(R.id.btnFilterAll)
        btnSeen = view.findViewById(R.id.btnFilterSeen)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EpisodesAdapter(emptyList(), { e -> e.id in seenIds }) {
            Toast.makeText(requireContext(), it.name, Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        btnAll.setOnClickListener {
            showingSeenOnly = false
            applyFilter()
        }
        btnSeen.setOnClickListener {
            showingSeenOnly = true
            applyFilter()
        }

        loadEpisodes()

        return view
    }

    private fun loadEpisodes(page: Int = 1) {
        RetrofitInstance.api.getEpisodes(page).enqueue(object : Callback<EpisodeResponse> {
            override fun onResponse(
                call: Call<EpisodeResponse>,
                response: Response<EpisodeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    allEpisodes = response.body()!!.results
                    applyFilter()
                } else {
                    Toast.makeText(requireContext(), "Error cargando episodios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun applyFilter() {
        val list = if (showingSeenOnly) {
            allEpisodes.filter { it.id in seenIds }
        } else {
            allEpisodes
        }
        adapter.submitList(list)
    }
}
