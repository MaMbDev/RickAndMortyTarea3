package dam.pmdm.rickandmortytarea3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.rickandmortytarea3.data.model.Episode
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesAdapter
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesViewModel
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesViewModelFactory

class EpisodesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAll: Button
    private lateinit var btnSeen: Button
    private lateinit var adapter: EpisodesAdapter


    private val viewModel: EpisodesViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            EpisodesViewModelFactory(requireActivity().application)
        )[EpisodesViewModel::class.java]
    }

    private var showingSeenOnly = false

    companion object {
        private const val TAG = "EpisodesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_episodes, container, false)

        recyclerView = view.findViewById(R.id.rvEpisodes)
        btnAll = view.findViewById(R.id.btnFilterAll)
        btnSeen = view.findViewById(R.id.btnFilterSeen)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "EpisodesFragment creado")

        setupRecyclerView()
        setupObservers()
        setupListeners()


        viewModel.debugSeenEpisodes()


        viewModel.loadEpisodesIfNeeded()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = EpisodesAdapter(emptyList(),
            { episode ->
                // Verifica si el episodio está visto
                val isSeen = viewModel.isEpisodeSeen(episode.id)
                Log.d(TAG, "Episodio ${episode.id} - ${episode.name}: visto = $isSeen")
                isSeen
            },
            { episode ->
                Log.d(TAG, "Click en episodio: ${episode.id} - ${episode.name}")
                // Toggle visto/no visto al hacer click
                viewModel.toggleEpisodeSeen(episode.id)
                // Actualizar el adapter inmediatamente
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(),
                    "${episode.name} ${if (viewModel.isEpisodeSeen(episode.id)) "✓ VISTO" else "✗ NO VISTO"}",
                    Toast.LENGTH_SHORT).show()
                viewModel.debugSeenEpisodes()
            }
        )
        recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Observar episodios
        viewModel.episodes.observe(viewLifecycleOwner) { episodes ->
            Log.d(TAG, "Episodios observados: ${episodes.size}")
            applyFilter(episodes)
        }

        // Observar episodios vistos -
        viewModel.seenEpisodes.observe(viewLifecycleOwner) { seenIds ->
            Log.d(TAG, "Episodios vistos actualizados: ${seenIds.size}")
            Log.d(TAG, "Lista de vistos: $seenIds")
            // Actualizar el adapter cuando cambian los episodios vistos
            adapter.notifyDataSetChanged()
            // También actualizar el filtro
            if (showingSeenOnly) {
                viewModel.episodes.value?.let { applyFilter(it) }
            }
        }

        // Observa errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        // Observa loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                Toast.makeText(requireContext(), "Cargando episodios...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        btnAll.setOnClickListener {
            showingSeenOnly = false
            viewModel.episodes.value?.let { applyFilter(it) }
            Toast.makeText(requireContext(), "Mostrando todos los episodios", Toast.LENGTH_SHORT).show()
        }

        btnSeen.setOnClickListener {
            showingSeenOnly = true
            viewModel.episodes.value?.let { applyFilter(it) }
            Toast.makeText(requireContext(), "Mostrando solo episodios vistos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFilter(episodes: List<Episode>) {
        val filteredList = if (showingSeenOnly) {
            val seenIds = viewModel.seenEpisodes.value ?: emptySet()
            val filtered = episodes.filter { it.id in seenIds }
            Log.d(TAG, "Filtrado (vistos): ${filtered.size} de ${episodes.size}")
            filtered
        } else {
            Log.d(TAG, "Mostrando todos: ${episodes.size}")
            episodes
        }
        adapter.submitList(filteredList.toList())
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "EpisodesFragment visible")
        viewModel.episodes.value?.let { applyFilter(it) }
        adapter.notifyDataSetChanged()
    }
}