package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Track
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.example.playlistmaker.presentation.ui.adapters.tracks.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.AudioPlayerFragment
import com.example.playlistmaker.presentation.viewmodel.media.FavoriteTracksFragmentViewModel
import com.example.playlistmaker.utils.Debounce
import com.example.playlistmaker.utils.FavoritesState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FavoriteTracksFragmentBinding? = null
    private val binding get() = _binding!!
    private val debounce: Debounce = Debounce()
    private lateinit var trackAdapter: TrackAdapter
    private val viewModel by viewModel<FavoriteTracksFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.tracks)
            is FavoritesState.Empty -> showEmpty()
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.placeholderWithoutMedia.isVisible = false
        binding.trackRecycler.isVisible = true

        trackAdapter.updateTracks(tracks)
    }

    private fun showEmpty() {
        binding.placeholderWithoutMedia.isVisible = true
        binding.trackRecycler.isVisible = false
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if(debounce.clickDebounce()) {
                val trackJson = Gson().toJson(track)
                findNavController().navigate(R.id.action_mediaFragment_to_audioPlayerFragment, AudioPlayerFragment.createArgs(trackJson))
            }
        }
        binding.trackRecycler.adapter = trackAdapter
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}