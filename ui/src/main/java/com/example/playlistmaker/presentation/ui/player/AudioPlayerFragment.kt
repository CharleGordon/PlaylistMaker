package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.domain.models.AudioPlayerState
import com.example.domain.models.Track
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.presentation.ui.adapters.playlist.PlaylistBottomSheetAdapter
import com.example.playlistmaker.presentation.ui.root.RootActivity
import com.example.playlistmaker.presentation.viewmodel.player.AudioPlayerFragmentViewModel
import com.example.playlistmaker.utils.AddingStatus
import com.example.playlistmaker.utils.Debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private val viewModel by viewModel<AudioPlayerFragmentViewModel>()
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private var playlistAdapter: PlaylistBottomSheetAdapter? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val debounce: Debounce = Debounce()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        setupListeners()
        setupAdapters()
        darkeningBackground()

        val track = getTrackFromArguments()
        if (track != null) {
            viewModel.preparePlayer(track)
            viewModel.checkFavorite(track.trackId)
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.favoriteLiveData.observe(viewLifecycleOwner) { state ->
            val favoriteIconRes = if (state) R.drawable.active_favorite_button else R.drawable.like_track_button
            binding.likeTrackButton.setImageResource(favoriteIconRes)
        }

        viewModel.addingResult.observe(viewLifecycleOwner) { (status, playlistName) ->
            val b = _binding ?: return@observe
            val trackAddedIconRes = when (status) {
                AddingStatus.ADDED -> R.drawable.to_playlist_done
                AddingStatus.ALREADY_EXISTS -> R.drawable.add_track_button
            }
            val message = when (status) {
                AddingStatus.ADDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    getString(R.string.added_to_playlist, playlistName)
                }
                AddingStatus.ALREADY_EXISTS -> {
                    getString(R.string.already_in_playlist, playlistName)
                }
            }
            b.addTrackButton.setImageResource(trackAddedIconRes)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.playlistsLiveData.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter?.submitList(playlists)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        playlistAdapter = null
    }

    private fun render(state: AudioPlayerState) {
        val b = _binding ?: return

        state.track?.let { fillPlayerData(it) }

        b.playTrackButton.isEnabled = state.isPlayerReady
        b.trackDuration.text = state.playbackTime

        val playIconRes = if (state.isPlaying) R.drawable.pause_track_icon else R.drawable.play_track_icon
        b.playTrackButton.setImageResource(playIconRes)
    }

    private fun setupListeners() {
        binding.topPanel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.playTrackButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.likeTrackButton.setOnClickListener {
            getTrackFromArguments()?.let { it1 -> viewModel.onFavoriteClicked(it1) }
        }
        binding.addTrackButton.setOnClickListener {
            viewModel.fillData()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.btnNewPlaylistBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayerFragment_to_newPlaylistFragment)
        }
    }

    private fun setupAdapters() {
        playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
            if (debounce.clickDebounce()) {
                val track = getTrackFromArguments()
                if (track != null) {
                    viewModel.addTrackToPlaylist(track, playlist)
                }
            }
        }
        binding.rvPlaylistsBottomSheet.adapter = playlistAdapter
    }

    private fun darkeningBackground() {
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                _binding?.overlay?.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                _binding?.overlay?.alpha = (slideOffset + 1) / 2
            }
        })
    }

    private fun getTrackFromArguments(): Track? {

        val trackJson = arguments?.getString(TRACK_KEY)

        return trackJson?.let { Gson().fromJson(it, Track::class.java) }
    }

    private fun fillPlayerData(track: Track) {

        val b = _binding ?: return
        val coverArtwork = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(coverArtwork)
            .placeholder(R.drawable.placeholder_image512x512)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(b.audioPlayerTrackImage)

        b.trackNameBelowImage.text = track.trackName
        b.artistNameBelowImage.text = track.artistName
        b.trackLength.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName != null) {
            b.collectionName.text = track.collectionName
            b.albumGroup.isVisible = true
        } else {
            b.albumGroup.isVisible= false
        }

        if (track.releaseDate != null) {
            b.releaseYear.text = track.releaseDate?.substring(0, 4).orEmpty()
            b.yearGroup.isVisible = true
        } else {
            b.yearGroup.isVisible = false
        }

        b.primaryGenreName.text = track.primaryGenreName
        b.country.text = track.country
    }

    companion object {
        const val TRACK_KEY = "track_data"

        fun createArgs(trackJson: String) : Bundle? =
            Bundle().apply {
                putString(TRACK_KEY, trackJson)
            }

    }
}