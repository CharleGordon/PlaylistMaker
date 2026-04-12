package com.example.playlistmaker.presentation.viewmodel.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.PlaylistInteractor
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.launch


class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> = _playlistDeleted

    private val _tracks = MutableLiveData<List<Track>?>()
    val tracks: MutableLiveData<List<Track>?> = _tracks

    fun getData() {
        viewModelScope.launch {
            val playlistData = interactor.getPlaylistById(playlistId)
            _playlist.postValue(playlistData)

            val ids = if (playlistData.trackIds?.isBlank() == true) {
                emptyList()
            } else {
                playlistData.trackIds
                    ?.replace("[", "")
                    ?.replace("]", "")
                    ?.replace(" ", "")
                    ?.split(",")
                    ?.filter { it.isNotEmpty() }
                    ?.map { it.toInt() } ?: emptyList()
            }

            val trackList = ids.let { interactor.getTracksByIds(it) }
            _tracks.postValue(trackList)
        }
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            val currentPlaylist = _playlist.value ?: return@launch
            interactor.deleteTrackFromPlaylist(trackId, currentPlaylist)
            getData()
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val currentPlaylist = _playlist.value ?: return@launch
            interactor.deletePlaylist(currentPlaylist)
            _playlistDeleted.postValue(true)
        }
    }
}