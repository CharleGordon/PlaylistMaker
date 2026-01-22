package com.example.playlistmaker.presentation.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        setupListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.themeSettings.observe(this) { themeSettings ->
            binding.nightThemeSwitcher.isChecked = themeSettings.isDarkTheme
        }
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.supportMessage.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.onTermsClicked()
        }

        binding.nightThemeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}