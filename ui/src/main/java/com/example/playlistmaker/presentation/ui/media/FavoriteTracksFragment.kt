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

    private var _binding: FavoriteTracksFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksFragmentViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FavoriteTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}