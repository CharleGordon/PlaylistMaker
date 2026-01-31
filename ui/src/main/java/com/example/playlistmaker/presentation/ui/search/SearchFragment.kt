package com.example.playlistmaker.presentation.ui.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.presentation.ui.adapters.tracks.TrackAdapter
import com.example.playlistmaker.presentation.ui.player.AudioPlayerFragment
import com.example.playlistmaker.presentation.viewmodel.search.SearchFragmentViewModel
import com.example.playlistmaker.utils.SearchState
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SearchFragmentViewModel>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var trackHistoryAdapter: TrackAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupAdapters()
        observeViewModel()

    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.searchText.observe(viewLifecycleOwner) { text ->
            if (binding.inputSearchText.text.toString() != text) {
                binding.inputSearchText.setText(text)
            }
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

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(mutableListOf()) { track ->
            if(clickDebounce()) {
                val trackJson = Gson().toJson(track)
                viewModel.onTrackClicked(track)
                findNavController().navigate(R.id.action_searchFragment2_to_audioPlayerFragment, AudioPlayerFragment.createArgs(trackJson))
            }
        }
        trackHistoryAdapter = TrackAdapter(mutableListOf()) { track ->
            if (clickDebounce()) {
                val trackJson = Gson().toJson(track)
                viewModel.onTrackClicked(track)
                findNavController().navigate(R.id.action_searchFragment2_to_audioPlayerFragment, AudioPlayerFragment.createArgs(trackJson))
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

        binding.clearIcon.setOnClickListener {
            binding.inputSearchText.setText("")
            hideKeyboard()
            viewModel.onClearSearchClicked()
        }

        binding.inputSearchText.doOnTextChanged { text, _, _, _ ->
            binding.clearIcon.visibility = clearButtonVisibility(text)
            viewModel.updateSearchText(text.toString())
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
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
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

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}