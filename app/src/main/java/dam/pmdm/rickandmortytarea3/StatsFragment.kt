package dam.pmdm.rickandmortytarea3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import dam.pmdm.rickandmortytarea3.databinding.FragmentStatsBinding
import dam.pmdm.rickandmortytarea3.ui.episodes.EpisodesViewModel

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EpisodesViewModel by activityViewModels()

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

        Log.d(TAG, "StatsFragment creado")

        // Email del usuario
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario"
        binding.tvUserEmail.text = "Usuario: $userEmail"

        setupObservers()

        viewModel.debugSeenEpisodes()

        viewModel.loadEpisodesIfNeeded()
    }

    private fun setupObservers() {

        viewModel.seenEpisodes.observe(viewLifecycleOwner) { seenIds ->
            Log.d(TAG, "Episodios vistos actualizados (Stats): ${seenIds.size}")
            updateStats(seenIds.size)
        }


        viewModel.episodes.observe(viewLifecycleOwner) { episodes ->
            Log.d(TAG, "Episodios actualizados (Stats): ${episodes.size}")
            if (episodes.isNotEmpty()) {
                updateStats(viewModel.seenEpisodes.value?.size ?: 0)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.e(TAG, "Error en ViewModel: $error")
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStats(seenCount: Int) {
        val totalEpisodes = viewModel.episodes.value?.size ?: 0

        binding.tvTotalEpisodes.text = "${getString(R.string.total_episodes)}: $totalEpisodes"
        binding.tvSeenEpisodes.text = "${getString(R.string.seen_episodes)}: $seenCount"

        if (totalEpisodes > 0) {
            val percentage = (seenCount.toFloat() / totalEpisodes * 100).toInt()
            binding.tvProgressPercentage.text = "$percentage%"
            binding.circularProgress.progress = percentage
            updateProgressMessage(percentage, seenCount, totalEpisodes)
        } else {
            binding.tvProgressPercentage.text = "0%"
            binding.circularProgress.progress = 0
            binding.tvProgressMessage.text = getString(R.string.loading_data)
        }
    }

    private fun updateProgressMessage(percentage: Int, seenCount: Int, totalEpisodes: Int) {
        val message = when {
            percentage == 0 -> "¡Comienza a ver episodios!"
            percentage < 25 -> "¡Sigue así!"
            percentage < 50 -> "¡Vas por buen camino!"
            percentage < 75 -> "¡Más de la mitad!"
            percentage < 100 -> "¡Casi terminado!"
            percentage == 100 -> "¡Completado! 🎉"
            else -> "¡Sigue viendo episodios!"
        }

        binding.tvProgressMessage.text = message

        // Si está completo, mostrar emoji de celebración
        if (percentage == 100) {
            binding.tvProgressMessage.setTextColor(requireContext().getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvProgressMessage.setTextColor(requireContext().getColor(android.R.color.darker_gray))
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