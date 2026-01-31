package com.example.playlistmaker.presentation.viewmodel.search

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.domain.api.SearchHistoryInteractor
import com.example.domain.api.TrackInteractor
import com.example.domain.models.Track
import com.example.playlistmaker.utils.SearchState

class SearchFragmentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    private val stateLiveData = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = stateLiveData
    private var lastLoadedState: SearchState.Content? = null
    val searchText = savedStateHandle.getLiveData<String>(KEY_SEARCH_TEXT, "")

    init {
        showHistory()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    fun searchDebounce(searchText: String) {
        if (searchText == lastSearchText) {
            return
        }
        this.lastSearchText = searchText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Loading)

            trackInteractor.searchTracks(searchText, object : TrackInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?) {
                    handler.post {
                        if (foundTracks != null) {
                            if (foundTracks.isNotEmpty()) {
                                lastLoadedState = SearchState.Content(foundTracks)
                                stateLiveData.postValue(lastLoadedState!!)
                            } else {
                                stateLiveData.postValue(SearchState.Error("Ничего не нашлось"))
                            }
                        } else {
                            stateLiveData.postValue(SearchState.Error("Ошибка сети"))
                        }
                    }
                }
            })
        }
    }

    fun showHistory() {
        val historyTracks = searchHistoryInteractor.readHistory()
        if (historyTracks.isNotEmpty()) {
            stateLiveData.postValue(SearchState.History(historyTracks))
        } else {
            stateLiveData.postValue(SearchState.Default)
        }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.clearHistory()
        stateLiveData.postValue(SearchState.Default)
    }

    fun onClearSearchClicked() {
        handler.removeCallbacks(searchRunnable)
        lastSearchText = null
        lastLoadedState = null
        showHistory()
    }

    fun onResume() {
        if (lastLoadedState != null) {
            stateLiveData.postValue(lastLoadedState!!)
        } else {
            showHistory()
        }
    }

    fun updateSearchText(text: String) {
        savedStateHandle[KEY_SEARCH_TEXT] = text
    }

    companion object {
        const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}