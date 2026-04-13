package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.domain.models.Playlist
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistFragmentBinding
import com.example.playlistmaker.presentation.ui.adapters.playlist.PlaylistAdapter
import com.example.playlistmaker.presentation.viewmodel.media.PlaylistFragmentViewModel
import com.example.playlistmaker.utils.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: PlaylistFragmentBinding? = null
    private val binding get() = _binding!!
    private var adapter: PlaylistAdapter? = null
    private val viewModel by viewModel<PlaylistFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = PlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter { playlist ->

            val bundle = Bundle().apply {
                putInt("playlistId", playlist.id)
            }

            findNavController().navigate(R.id.action_mediaFragment_to_playlistDetailsFragment, bundle)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsState.Content -> showContent(state.playlists)
                is PlaylistsState.Empty -> showEmpty()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.placeholderWithoutPlaylist.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        adapter?.submitList(playlists)
    }

    private fun showEmpty() {
        binding.placeholderWithoutPlaylist.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = PlaylistFragment()
    }
}