package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
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
    private lateinit var placeholderWithoutTextMessage: LinearLayout
    private lateinit var placeholderServerErrorMessage: LinearLayout
    private lateinit var refreshButton: Button

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
        recyclerView = findViewById(R.id.trackRecycler)
        trackList = mutableListOf()
        trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter
        placeholderWithoutTextMessage = findViewById(R.id.placeholderWithoutTextMessage)
        placeholderServerErrorMessage = findViewById(R.id.placeholderServerErrorMessage)
        refreshButton = findViewById(R.id.refreshButton)

        refreshButton.setOnClickListener {
            performSearch(inputEditText.text.toString())
        }

        arrowBackIcon.setNavigationOnClickListener {
            finish()
        }

        clearIcon.setOnClickListener {
            inputEditText.setText("")

            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

            clearTrackList()
            showPlaceholder(SearchResultState.DEFAULT)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString())
                true
            }
            false
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
            }
            SearchResultState.NO_RESULTS -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.VISIBLE
                placeholderServerErrorMessage.visibility = View.GONE
            }
            SearchResultState.SERVER_ERROR -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.VISIBLE
            }
            SearchResultState.DEFAULT -> {
                recyclerView.visibility = View.GONE
                placeholderWithoutTextMessage.visibility = View.GONE
                placeholderServerErrorMessage.visibility = View.GONE
            }
        }
    }

    private fun performSearch(searchText: String) {
        val searchService = RetrofitClient.searchApi

        searchService.search(searchText).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
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
                showPlaceholder(SearchResultState.SERVER_ERROR)
            }
        })
    }

    private fun clearTrackList() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    companion object { const val KEY_SEARCH_TEXT = "SEARCH_TEXT" }
}