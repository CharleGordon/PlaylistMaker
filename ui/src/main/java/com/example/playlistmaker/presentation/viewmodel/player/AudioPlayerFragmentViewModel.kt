package com.example.playlistmaker.presentation.viewmodel.player

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.FavoritesInteractor
import com.example.domain.api.PlaylistInteractor
import com.example.domain.models.AudioPlayerState
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import com.example.playlistmaker.utils.AddingStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragmentViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val playerStateLiveData = MutableLiveData<AudioPlayerState>()
    val playerState: LiveData<AudioPlayerState> = playerStateLiveData
    private val _favoriteLiveData = MutableLiveData<Boolean>()
    val favoriteLiveData: LiveData<Boolean> = _favoriteLiveData
    private val _playlistsLiveData = MutableLiveData<List<Playlist>>()
    val playlistsLiveData: LiveData<List<Playlist>> = _playlistsLiveData
    private val _addingResult = MutableLiveData<Pair<AddingStatus, String>>()
    val addingResult: LiveData<Pair<AddingStatus, String>> = _addingResult

    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null

    fun fillData() {
        playlistInteractor.getAllPlaylists()
            .onEach { playlists ->
                _playlistsLiveData.postValue(playlists)
            }
            .launchIn(viewModelScope)
    }

    fun checkFavorite(trackId: Int) {
        viewModelScope.launch {
            _favoriteLiveData.postValue(favoritesInteractor.isFavorite(trackId))
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            if (favoriteLiveData.value == true) {
                favoritesInteractor.removeTrack(track)
                _favoriteLiveData.postValue(false)
            } else {
                favoritesInteractor.addTrack(track)
                _favoriteLiveData.postValue(true)
            }
        }
    }

    fun addTrackToPlaylist(track: Track?, playlist: Playlist) {
        if (track == null) return

        val cleanedIds = playlist.trackIds
            ?.replace("[", "")
            ?.replace("]", "")
            ?.replace(" ", "")

        val trackIdsList = if (cleanedIds?.isEmpty() == true) {
            mutableListOf<Long>()
        } else {
            cleanedIds?.split(",")
                ?.mapNotNull { it.toLongOrNull() }
                ?.toMutableList()
        }

        if (trackIdsList != null) {
            if (trackIdsList.contains(track.trackId.toLong())) {
                _addingResult.postValue(AddingStatus.ALREADY_EXISTS to playlist.title)
            } else {
                viewModelScope.launch {
                    playlistInteractor.addTrackToPlaylist(track, playlist)
                    _addingResult.postValue(AddingStatus.ADDED to playlist.title)
                    fillData()
                }
            }
        }
    }

    fun preparePlayer(track: Track) {
        playerStateLiveData.value = AudioPlayerState(track = track, playbackTime = "00:00")

        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(
                playerStateLiveData.value?.copy(isPlayerReady = true)
            )
        }
        mediaPlayer.setOnCompletionListener {
            stopTimer()
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
        startTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerStateLiveData.postValue(playerStateLiveData.value?.copy(isPlaying = false))
        stopTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(REFRESH_DURATION_DELAY)
                val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

                playerStateLiveData.postValue(playerStateLiveData.value?.copy(playbackTime = formattedTime))

            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        stopTimer()
    }

    companion object {
        private const val REFRESH_DURATION_DELAY = 300L
    }

}