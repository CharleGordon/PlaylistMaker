package com.example.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.ui.settings.dark_theme.App
import com.example.playlistmaker.presentation.ui.player.AudioPlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.dto.SearchResponse
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.tracks.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private enum class SearchResultState {
        SUCCESS,
        LOADING,
        NO_RESULTS,
        SERVER_ERROR,
        DEFAULT,
        HISTORY
    }

    private var searchText = ""
    private lateinit var inputEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackList: MutableList<Track>
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private lateinit var placeholderWithoutTextMessage: LinearLayout
    private lateinit var placeholderServerErrorMessage: LinearLayout
    private lateinit var searchHistoryView: ScrollView
    private lateinit var clearSearchHistoryButton: MaterialButton
    private lateinit var refreshButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private lateinit var arrowBackIcon: MaterialToolbar
    private lateinit var clearIcon: ImageView
    private lateinit var searchHistoryRecycler: RecyclerView
    private val historyTrackList = mutableListOf<Track>()
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch(inputEditText.text.toString()) }
    private val trackInteractor = Creator.provideTracksInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        applyWindowInsets()

        initsViews()

        setupAdapters()

        initSearchHistory()

        updateHistoryList()

        setupListeners()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val savedSearchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")

        inputEditText.setText(savedSearchText)
        searchText = savedSearchText
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initsViews() {
        arrowBackIcon = findViewById(R.id.searchArrowBack)
        inputEditText = findViewById(R.id.inputSearchText)
        clearIcon = findViewById(R.id.clearIcon)
        searchHistoryRecycler = findViewById(R.id.searchHistoryRecycler)
        recyclerView = findViewById(R.id.trackRecycler)
        placeholderWithoutTextMessage = findViewById(R.id.placeholderWithoutTextMessage)
        placeholderServerErrorMessage = findViewById(R.id.placeholderServerErrorMessage)
        searchHistoryView = findViewById(R.id.searchHistoryView)
        clearSearchHistoryButton = findViewById(R.id.clearSearchHistoryButton)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupAdapters() {
        trackList = mutableListOf()
        trackAdapter = TrackAdapter(trackList) { track ->
            if(clickDebounce()) {
                trackToPlayerIntent(track)
            }
        }
        trackHistoryAdapter = TrackAdapter(historyTrackList) { track ->
            if (clickDebounce()) {
                trackToPlayerIntent(track)
            }
        }
        searchHistoryRecycler.adapter = trackHistoryAdapter
        recyclerView.adapter = trackAdapter
    }

    private fun setupListeners() {
        clearSearchHistoryButton.setOnClickListener {
            searchHistory.clear()
            historyTrackList.clear()
            trackHistoryAdapter.notifyDataSetChanged()
            searchHistoryView.visibility = View.GONE
        }

        refreshButton.setOnClickListener {
            performSearch(inputEditText.text.toString())
        }

        arrowBackIcon.setNavigationOnClickListener {
            finish()
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            clearTrackList()
            showPlaceholder(SearchResultState.DEFAULT)
            updateHistoryList()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = clearButtonVisibility(s)
                searchDebounce()

                if (inputEditText.hasFocus() && s?.isEmpty() == true && historyTrackList.isNotEmpty()) {
                    showPlaceholder(SearchResultState.HISTORY)
                } else {
                    searchHistoryView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTrackList.isNotEmpty()) {
                showPlaceholder(SearchResultState.HISTORY)
            } else {
                searchHistoryView.visibility = View.GONE
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showPlaceholder(state: SearchResultState) {
        when (state) {
            SearchResultState.LOADING -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            SearchResultState.SUCCESS -> {
                recyclerView.visibility = View.VISIBLE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            SearchResultState.NO_RESULTS -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.VISIBLE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            SearchResultState.SERVER_ERROR -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.VISIBLE
                searchHistoryView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            SearchResultState.DEFAULT -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
            SearchResultState.HISTORY -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun performSearch(searchText: String) {
        if (searchText.isEmpty()) return

        showPlaceholder(SearchResultState.LOADING)

        trackInteractor.searchTracks(searchText, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                handler.post {
                    if (foundTracks != null) {
                        if (foundTracks.isNotEmpty()) {
                            trackList.clear()
                            trackList.addAll(foundTracks)
                            trackAdapter.notifyDataSetChanged()
                            showPlaceholder(SearchResultState.SUCCESS)
                        } else {
                            showPlaceholder(SearchResultState.NO_RESULTS)
                        }
                    } else {
                        showPlaceholder(SearchResultState.SERVER_ERROR)
                    }
                }
            }
        })
    }

    private fun clearTrackList() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun initSearchHistory() {
        val sharedPref = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPref)
    }

    private fun updateHistoryList() {
        val newHistory = searchHistory.read()

        historyTrackList.clear()
        historyTrackList.addAll(newHistory)
        trackHistoryAdapter.notifyDataSetChanged()

        if (historyTrackList.isNotEmpty() && inputEditText.text.isEmpty() && inputEditText.hasFocus()) {
            searchHistoryView.visibility = View.VISIBLE
        } else {
            searchHistoryView.visibility = View.GONE
        }
    }

    private fun trackToPlayerIntent(track: Track) {
        searchHistory.add(track)
        updateHistoryList()

        val playerIntent = Intent(this, AudioPlayerActivity::class.java)
        val gson = Gson()
        val trackJson = gson.toJson(track)

        playerIntent.putExtra(AudioPlayerActivity.TRACK_KEY, trackJson)
        startActivity(playerIntent)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        if (inputEditText.text.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    companion object {
        const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}