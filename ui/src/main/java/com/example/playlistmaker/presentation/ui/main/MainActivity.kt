package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.presentation.ui.media.MediaActivity
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity
import com.example.playlistmaker.presentation.viewmodel.main.MainActivityViewModel
import com.example.playlistmaker.utils.NavigationCommand

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        setupListeners()

        observeViewModel()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.search.setOnClickListener {
            viewModel.onSearchClicked()
        }

        binding.media.setOnClickListener {
            viewModel.onMediaClicked()
        }

        binding.settingsButton.setOnClickListener {
            viewModel.onSettingsClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.navigationCommand.observe(this) { command ->
            when (command) {
                is NavigationCommand.GoToSearch -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                }
                is NavigationCommand.GoToMedia -> {
                    val intent = Intent(this, MediaActivity::class.java)
                    startActivity(intent)
                }
                is NavigationCommand.GoToSettings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}