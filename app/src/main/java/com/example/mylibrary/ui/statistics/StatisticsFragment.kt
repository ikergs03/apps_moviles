package com.example.mylibrary.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mylibrary.R
import com.example.mylibrary.databinding.FragmentStatisticsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            binding.tvTotal.text = getString(R.string.statistics_total, stats.total)
            binding.tvBooks.text = getString(R.string.statistics_books, stats.books)
            binding.tvMovies.text = getString(R.string.statistics_movies, stats.movies)
            binding.tvVideogames.text = getString(R.string.statistics_videogames, stats.videogames)
            binding.tvPending.text = getString(R.string.statistics_pending, stats.pending)
            binding.tvInProgress.text = getString(R.string.statistics_in_progress, stats.inProgress)
            binding.tvCompleted.text = getString(R.string.statistics_completed, stats.completed)
            binding.tvAbandoned.text = getString(R.string.statistics_abandoned, stats.abandoned)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
