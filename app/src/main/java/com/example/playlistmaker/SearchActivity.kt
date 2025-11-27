package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private enum class SearchResultState {
        SUCCESS,
        NO_RESULTS,
        SERVER_ERROR,
        DEFAULT
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
    private val historyTrackList = mutableListOf<Track>()
    private var isClickAllowed = true
    private val handler = android.os.Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch(inputEditText.text.toString()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val arrowBackIcon = findViewById<MaterialToolbar>(R.id.searchArrowBack)
        inputEditText = findViewById(R.id.inputSearchText)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)
        val searchHistoryRecycler = findViewById<RecyclerView>(R.id.searchHistoryRecycler)
        recyclerView = findViewById(R.id.trackRecycler)
        trackList = mutableListOf()
        trackAdapter = TrackAdapter(trackList) { track ->
            if(clickDebounce()) {
                searchHistory.add(track)
                updateHistoryList()

                val playerIntent = Intent(this, AudioPlayerActivity::class.java)
                val gson = Gson()
                val trackJson = gson.toJson(track)

                playerIntent.putExtra(AudioPlayerActivity.TRACK_KEY, trackJson)
                startActivity(playerIntent)
            }
        }
        trackHistoryAdapter = TrackAdapter(historyTrackList) { track ->
            if (clickDebounce()) {
                searchHistory.add(track)
                updateHistoryList()

                val playerIntent = Intent(this, AudioPlayerActivity::class.java)
                val gson = Gson()
                val trackJson = gson.toJson(track)

                playerIntent.putExtra(AudioPlayerActivity.TRACK_KEY, trackJson)
                startActivity(playerIntent)
            }
        }
        searchHistoryRecycler.adapter = trackHistoryAdapter
        recyclerView.adapter = trackAdapter
        placeholderWithoutTextMessage = findViewById(R.id.placeholderWithoutTextMessage)
        placeholderServerErrorMessage = findViewById(R.id.placeholderServerErrorMessage)
        searchHistoryView = findViewById(R.id.searchHistoryView)
        clearSearchHistoryButton = findViewById(R.id.clearSearchHistoryButton)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
        val sharedPref = getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPref)

        updateHistoryList()

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
                    recyclerView.visibility = View.GONE
                    placeholderWithoutTextMessage.visibility = View.GONE
                    placeholderServerErrorMessage.visibility = View.GONE
                    searchHistoryView.visibility = View.VISIBLE
                } else {
                    searchHistoryView.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
//                searchText = s?.toString() ?: ""
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty() && historyTrackList.isNotEmpty()) {
                searchHistoryView.visibility = View.VISIBLE
            } else {
                searchHistoryView.visibility = View.GONE
            }
        }
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

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showPlaceholder(state: SearchResultState) {
        when (state) {
            SearchResultState.SUCCESS -> {
                recyclerView.visibility = View.VISIBLE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
            }
            SearchResultState.NO_RESULTS -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.VISIBLE
                placeholderServerErrorMessage.visibility = View.GONE
                searchHistoryView.visibility = View.GONE
            }
            SearchResultState.SERVER_ERROR -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.VISIBLE
                searchHistoryView.visibility = View.GONE
            }
            SearchResultState.DEFAULT -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
            }
        }
    }

    private fun performSearch(searchText: String) {

        searchHistoryView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val searchService = RetrofitClient.searchApi

        searchService.search(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                progressBar.visibility = View.GONE
                if(response.isSuccessful) {
                    val trackResult = response.body()?.results
                    if(!trackResult.isNullOrEmpty()) {
                        trackList.clear()
                        trackList.addAll(trackResult)
                        trackAdapter.notifyDataSetChanged()
                        showPlaceholder(SearchResultState.SUCCESS)
                    } else {
                        showPlaceholder(SearchResultState.NO_RESULTS)
                    }
                } else {
                    showPlaceholder(SearchResultState.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showPlaceholder(SearchResultState.SERVER_ERROR)
            }
        })
    }

    private fun clearTrackList() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
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