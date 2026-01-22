package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.presentation.ui.tracks.TrackAdapter
import com.example.playlistmaker.presentation.viewmodel.search.SearchActivityViewModel
import com.example.playlistmaker.utils.ActivityNavigator
import com.example.playlistmaker.utils.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val KEY_SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModel<SearchActivityViewModel>()
    private var searchText = ""
    private lateinit var inputEditText: EditText
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val navigator by lazy { ActivityNavigator(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        setupListeners()
        setupAdapters()
        observeViewModel()

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            binding.inputSearchText.setText(searchText)
            viewModel.showHistory()
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchText = binding.inputSearchText.text.toString()
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    private fun render(state: SearchState) {
        binding.progressBar.isVisible = false
        binding.trackRecycler.isVisible = false
        binding.placeholderWithoutTextMessage.isVisible = false
        binding.placeholderServerErrorMessage.isVisible = false
        binding.searchHistoryView.isVisible = false

        when (state) {
            is SearchState.Loading -> binding.progressBar.isVisible = true
            is SearchState.Content -> {
                binding.trackRecycler.isVisible = true
                trackAdapter.updateTracks(state.tracks)
            }
            is SearchState.History -> {
                binding.searchHistoryView.isVisible = true
                trackHistoryAdapter.updateTracks(state.tracks)
            }
            is SearchState.Error -> {
                if (state.message == "Ошибка сети") {
                    binding.placeholderServerErrorMessage.isVisible = true
                } else {
                    binding.placeholderWithoutTextMessage.isVisible = true
                }
            }
            is SearchState.Default -> {
                binding.trackRecycler.adapter = trackAdapter
                trackAdapter.updateTracks(emptyList())
            }
        }
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

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if(clickDebounce()) {
                viewModel.onTrackClicked(track)
                navigator.openTrackPlayer(track)
            }
        }
        trackHistoryAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
                navigator.openTrackPlayer(track)
            }
        }
        binding.trackRecycler.adapter = trackAdapter
        binding.searchHistoryRecycler.adapter = trackHistoryAdapter
    }

    private fun setupListeners() {
        binding.clearSearchHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchRequest(binding.inputSearchText.text.toString())
        }

        binding.searchArrowBack.setNavigationOnClickListener {
            finish()
        }

        binding.clearIcon.setOnClickListener {
            binding.inputSearchText.setText("")
            hideKeyboard()
            viewModel.onClearSearchClicked()
        }

        binding.inputSearchText.doOnTextChanged { text, _, _, _ ->
            binding.clearIcon.visibility = clearButtonVisibility(text)
            viewModel.searchDebounce(text.toString())
        }

        binding.inputSearchText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputSearchText.text.isEmpty()) {
                viewModel.showHistory()
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.inputSearchText.windowToken, 0)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}