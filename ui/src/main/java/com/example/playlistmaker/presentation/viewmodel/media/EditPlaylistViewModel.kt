package com.example.playlistmaker.presentation.viewmodel.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.domain.api.PlaylistInteractor
import com.example.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistId: Int,
    override val interactor: PlaylistInteractor
) : NewPlaylistViewModel(interactor) {

    private val _playlistInfo = MutableLiveData<Playlist>()
    val playlistInfo: LiveData<Playlist> = _playlistInfo

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    init {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            _playlistInfo.postValue(playlist)
        }
    }

    fun saveChanges(newName: String, newDescription: String?) {
        viewModelScope.launch {
            val currentPlaylist = _playlistInfo.value ?: return@launch

            val finalImagePath = if (currentImageUri != null) {
                interactor.saveImageToPrivateStorage(currentImageUri.toString())
            } else {
                currentPlaylist.imagePath
            }

            val updatedPlaylist = currentPlaylist.copy(
                title = newName,
                description = newDescription,
                imagePath = finalImagePath
            )

            interactor.savePlaylist(updatedPlaylist)
            _isSaved.value = true
        }
    }
}