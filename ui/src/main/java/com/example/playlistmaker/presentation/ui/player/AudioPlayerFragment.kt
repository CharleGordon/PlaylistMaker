package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.domain.models.AudioPlayerState
import com.example.domain.models.Track
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.presentation.ui.root.RootActivity
import com.example.playlistmaker.presentation.viewmodel.player.AudioPlayerFragmentViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private val viewModel: AudioPlayerFragmentViewModel by viewModels()
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        val track = getTrackFromArguments()
        if (track != null) {
            viewModel.preparePlayer(track)
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()

        if (activity is RootActivity) {
            (activity as RootActivity).setBottomNavVisibility(false)
        }
    }

    override fun onPause() {
        super.onPause()

        if (activity is RootActivity) {
            (activity as RootActivity).setBottomNavVisibility(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: AudioPlayerState) {
        state.track?.let { fillPlayerData(it) }

        binding.playTrackButton.isEnabled = state.isPlayerReady
        binding.trackDuration.text = state.playbackTime

        val playIconRes = if (state.isPlaying) R.drawable.pause_track_icon else R.drawable.play_track_icon
        binding.playTrackButton.setImageResource(playIconRes)
    }

    private fun setupListeners() {
        binding.topPanel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.playTrackButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun getTrackFromArguments(): Track? {

        val trackJson = arguments?.getString(TRACK_KEY)

        return trackJson?.let { Gson().fromJson(it, Track::class.java) }
    }

    private fun fillPlayerData(track: Track) {

        val coverArtwork = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(coverArtwork)
            .placeholder(R.drawable.placeholder_image512x512)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(binding.audioPlayerTrackImage)

        binding.trackNameBelowImage.text = track.trackName
        binding.artistNameBelowImage.text = track.artistName
        binding.trackLength.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName != null) {
            binding.collectionName.text = track.collectionName
            binding.albumGroup.isVisible = true
        } else {
            binding.albumGroup.isVisible= false
        }

        if (track.releaseDate != null) {
            binding.releaseYear.text = track.releaseDate?.substring(0, 4).orEmpty()
            binding.yearGroup.isVisible = true
        } else {
            binding.yearGroup.isVisible = false
        }

        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country
    }

    companion object {
        const val TRACK_KEY = "track_data"

        fun createArgs(trackJson: String) : Bundle? =
            Bundle().apply {
                putString(TRACK_KEY, trackJson)
            }

    }
}