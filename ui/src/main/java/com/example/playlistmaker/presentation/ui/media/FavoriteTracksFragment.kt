package com.example.playlistmaker.presentation.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.databinding.FavoriteTracksFragmentBinding
import com.example.playlistmaker.presentation.viewmodel.media.FavoriteTracksFragmentViewModel

class FavoriteTracksFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }

    private lateinit var binding: FavoriteTracksFragmentBinding
    private val viewModel: FavoriteTracksFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}