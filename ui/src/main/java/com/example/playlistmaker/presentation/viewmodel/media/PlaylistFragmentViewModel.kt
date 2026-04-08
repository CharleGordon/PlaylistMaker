package com.example.playlistmaker.presentation.viewmodel.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.PlaylistInteractor
import com.example.domain.models.Playlist
import com.example.playlistmaker.utils.PlaylistsState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlaylistFragmentViewModel(
    private val interactor: PlaylistInteractor) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> = _state

    fun fillData() {
        interactor.getAllPlaylists()
            .onEach { playlists ->
                processResult(playlists)
            }
            .launchIn(viewModelScope)
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            _state.postValue(PlaylistsState.Empty)
        } else {
            _state.postValue(PlaylistsState.Content(playlists))
        }
    }

}