package com.example.playlistmaker.presentation.viewmodel.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.api.SearchHistoryInteractor
import com.example.domain.api.TrackInteractor
import com.example.domain.models.Track
import com.example.playlistmaker.utils.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

class SearchFragmentViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private var searchJob: Job? = null

    private var lastSearchText: String? = null

    private val stateLiveData = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = stateLiveData
    private var lastLoadedState: SearchState.Content? = null
    val searchText = savedStateHandle.getLiveData<String>(KEY_SEARCH_TEXT, "")

    init {
        showHistory()
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }

    fun searchDebounce(searchText: String) {
        if (searchText == lastSearchText) {
            return
        }
        this.lastSearchText = searchText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(searchText)
        }
    }

    fun searchRequest(searchText: String) {
        if (searchText.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Loading)

            viewModelScope.launch {
                trackInteractor
                    .searchTracks(searchText)
                    .onEach { pair ->
                        processResult(pair.first, pair.second)
                    }
                    .launchIn(viewModelScope)
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                stateLiveData.postValue(SearchState.Error("Ошибка сети"))
            }

            foundTracks.isNullOrEmpty() -> {
                stateLiveData.postValue(SearchState.Error("Ничего не нашлось"))
            }

            else -> {
                lastLoadedState = SearchState.Content(foundTracks)
                stateLiveData.postValue(lastLoadedState!!)
            }
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
        searchJob?.cancel()
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