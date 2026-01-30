package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.PlaylistFragmentBinding
import com.example.playlistmaker.presentation.viewmodel.media.PlaylistFragmentViewModel

class PlaylistFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistFragment()
    }

    private lateinit var binding: PlaylistFragmentBinding
    private val viewModel: PlaylistFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = PlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}