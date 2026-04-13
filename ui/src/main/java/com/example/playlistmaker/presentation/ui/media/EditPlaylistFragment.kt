package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.viewmodel.media.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import com.example.domain.models.Playlist

class EditPlaylistFragment : NewPlaylistFragment() {

    private val playlistId by lazy { arguments?.getInt("playlistId") ?: 0 }

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topPanel.setTitle(getString(R.string.edit_playlist_title))
        binding.createButton.text = getString(R.string.save_playlist_button)

        viewModel.playlistInfo.observe(viewLifecycleOwner) { playlist ->
            fillData(playlist)
        }

        viewModel.isSaved.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun fillData(playlist: Playlist) {
        binding.inputPlaylistName.setText(playlist.title)
        binding.inputPlaylistDescription.setText(playlist.description)

        Glide.with(this)
            .load(playlist.imagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(binding.addPlaylistImage)

    }

    override fun setupCreateButton() {
        val name = binding.inputPlaylistName.text.toString()
        val description = binding.inputPlaylistDescription.text.toString()

        viewModel.saveChanges(name, description)
    }

    override fun handleBackNavigation() {
        findNavController().popBackStack()
    }
}