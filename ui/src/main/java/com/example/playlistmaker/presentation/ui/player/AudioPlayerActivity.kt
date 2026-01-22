package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.domain.models.AudioPlayerState
import com.example.playlistmaker.R
import com.example.domain.models.Track
import com.example.playlistmaker.databinding.AudioPlayerViewBinding
import com.example.playlistmaker.presentation.viewmodel.player.AudioPlayerActivityViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "track_data"
    }

    private val viewModel: AudioPlayerActivityViewModel by viewModels()
    private lateinit var bindind: AudioPlayerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindind = AudioPlayerViewBinding.inflate(layoutInflater)
        setContentView(bindind.root)
        applyWindowInsets()
        setupListeners()

        val track = getTrackFromIntent()
        if (track != null) {
            viewModel.preparePlayer(track)
        }

        viewModel.playerState.observe(this) { state ->
            render(state)
        }

    }

    private fun render(state: AudioPlayerState) {
        state.track?.let { fillPlayerData(it) }

        bindind.playTrackButton.isEnabled = state.isPlayerReady
        bindind.trackDuration.text = state.playbackTime

        val playIconRes = if (state.isPlaying) R.drawable.pause_track_icon else R.drawable.play_track_icon
        bindind.playTrackButton.setImageResource(playIconRes)
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audioPlayerView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        bindind.topPanel.setNavigationOnClickListener {
            finish()
        }

        bindind.playTrackButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun getTrackFromIntent(): Track? {

        val trackJson = intent.getStringExtra(TRACK_KEY)

        return trackJson?.let { Gson().fromJson(it, Track::class.java) }
    }

    private fun fillPlayerData(track: Track) {

        val coverArtwork = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        Glide.with(this)
            .load(coverArtwork)
            .placeholder(R.drawable.placeholder_image512x512)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(bindind.audioPlayerTrackImage)

        bindind.trackNameBelowImage.text = track.trackName
        bindind.artistNameBelowImage.text = track.artistName
        bindind.trackLength.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        if (track.collectionName != null) {
            bindind.collectionName.text = track.collectionName
            bindind.albumGroup.isVisible = true
        } else {
            bindind.albumGroup.isVisible= false
        }

        if (track.releaseDate != null) {
            bindind.releaseYear.text = track.releaseDate?.substring(0, 4).orEmpty()
            bindind.yearGroup.isVisible = true
        } else {
            bindind.yearGroup.isVisible = false
        }

        bindind.primaryGenreName.text = track.primaryGenreName
        bindind.country.text = track.country
    }
}