package dam.pmdm.rickandmortytarea3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import dam.pmdm.rickandmortytarea3.databinding.FragmentStatsBinding
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesViewModel
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesViewModelFactory

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!


    private lateinit var viewModel: EpisodesViewModel

    companion object {
        private const val TAG = "StatsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa ViewModel
        viewModel = ViewModelProvider(
            requireActivity(),
            EpisodesViewModelFactory(requireActivity().application)
        )[EpisodesViewModel::class.java]

        Log.d(TAG, "StatsFragment creado")

        // Email del usuario
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"
        binding.tvUserEmail.text = "Usuario: $userEmail"

        setupObservers()


        viewModel.debugSeenEpisodes()

        viewModel.loadEpisodesIfNeeded()
    }

    private fun setupObservers() {
        // Observa episodios vistos
        viewModel.seenEpisodes.observe(viewLifecycleOwner) { seenIds ->
            Log.d(TAG, "Episodios vistos actualizados (Stats): ${seenIds.size}")
            updateStats(seenIds.size)
        }

        // Observa todos los episodios
        viewModel.episodes.observe(viewLifecycleOwner) { episodes ->
            Log.d(TAG, "Episodios actualizados (Stats): ${episodes.size}")
            if (episodes.isNotEmpty()) {
                updateStats(viewModel.seenEpisodes.value?.size ?: 0)
            }
        }

        // Observa errores
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e(TAG, "Error en ViewModel: $error")
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStats(seenCount: Int) {
        val totalEpisodes = viewModel.episodes.value?.size ?: 0

        binding.tvTotalEpisodes.text = "Total episodios: $totalEpisodes"
        binding.tvSeenEpisodes.text = "Episodios vistos: $seenCount"

        if (totalEpisodes > 0) {
            val percentage = (seenCount.toFloat() / totalEpisodes * 100).toInt()
            binding.tvPercentage.text = "Progreso: $percentage%"

            // Actualiza progress bar
            binding.progressBar.max = totalEpisodes
            binding.progressBar.progress = seenCount

            Log.d(TAG, "Estadísticas actualizadas: $seenCount/$totalEpisodes ($percentage%)")
        } else {
            binding.tvPercentage.text = "Progreso: 0%"
            binding.progressBar.max = 100
            binding.progressBar.progress = 0
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "StatsFragment visible, actualizando estadísticas")
        // Actualiza estadísticas cuando el fragment se vuelve visible
        updateStats(viewModel.seenEpisodes.value?.size ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}