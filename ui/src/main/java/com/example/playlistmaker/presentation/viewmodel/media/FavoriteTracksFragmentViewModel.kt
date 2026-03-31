package com.example.playlistmaker.presentation.viewmodel.media

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.FavoritesInteractor
import com.example.domain.models.Track
import com.example.playlistmaker.utils.FavoritesState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FavoriteTracksFragmentViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = stateLiveData

    fun fillData() {
        favoritesInteractor
            .getFavoriteTracks()
            .onEach { tracks ->
                processResult(tracks)
            }
            .launchIn(viewModelScope)
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            stateLiveData.postValue(FavoritesState.Empty)
        } else {
            stateLiveData.postValue(FavoritesState.Content(tracks))
        }
    }
}