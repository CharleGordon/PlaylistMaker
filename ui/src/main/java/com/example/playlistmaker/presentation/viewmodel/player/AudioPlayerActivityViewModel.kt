package com.example.playlistmaker.presentation.viewmodel.player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.models.AudioPlayerState
import com.example.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivityViewModel: ViewModel() {

    companion object {
        private const val REFRESH_DURATION_DELAY = 300L
    }

    private val playerStateLiveData = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> = playerStateLiveData

    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = Runnable { updatePlaybackTime() }

    fun preparePlayer(track: Track) {
        playerStateLiveData.value = AudioPlayerState(track = track)

        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(
                playerStateLiveData.value?.copy(isPlayerReady = true)
            )
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTimeRunnable)
            playerStateLiveData.postValue(
                playerStateLiveData.value?.copy(isPlaying = false, playbackTime = "00:00")
            )
        }
    }

    fun onPlayButtonClicked() {
        val currentState = playerStateLiveData.value ?: return
        if (currentState.isPlaying) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStateLiveData.postValue(playerStateLiveData.value?.copy(isPlaying = true))
        handler.post(updateTimeRunnable)
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(playerStateLiveData.value?.copy(isPlaying = false))
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updatePlaybackTime() {
        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        playerStateLiveData.postValue(playerStateLiveData.value?.copy(playbackTime = formattedTime))
        handler.postDelayed(updateTimeRunnable, REFRESH_DURATION_DELAY)
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeRunnable)
    }

}