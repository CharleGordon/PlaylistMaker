package com.example.playlistmaker.presentation.viewmodel.media

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.PlaylistInteractor
import com.example.domain.models.Playlist
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    protected open val interactor: PlaylistInteractor
) : ViewModel() {

    var currentImageUri: Uri? = null
        protected set

    private val _isCreateButtonEnabled = MutableLiveData<Boolean>(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    fun onNameChanged(text: String?) {
        _isCreateButtonEnabled.value = !text.isNullOrBlank()
    }

    open fun saveTempImage(uri: Uri) {
        viewModelScope.launch {
            val internalPath = interactor.saveImageToPrivateStorage(uri.toString())

            currentImageUri = internalPath.toUri()
        }
    }

    fun createPlaylist(title: String, description: String?) {
        viewModelScope.launch {

            val newPlaylist = Playlist(
                title = title,
                description = description,
                imagePath = currentImageUri.toString(),
                trackIds = "",
                tracksCount = 0
            )

            interactor.savePlaylist(newPlaylist)
        }
    }

    fun hasUnsavedData(title: String?, description: String?): Boolean {
        return !title.isNullOrBlank() || !description.isNullOrBlank() || currentImageUri != null
    }
}