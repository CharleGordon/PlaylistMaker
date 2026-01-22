package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.domain.api.ThemeInteractor
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsActivityViewModel
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsActivityViewModel by viewModels {
        SettingsViewModelFactory(
            sharingInteractor = Creator.provideSharingInteractor(this),
            themeInteractor = Creator.provideThemeInteractor(this)
        )
    }

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