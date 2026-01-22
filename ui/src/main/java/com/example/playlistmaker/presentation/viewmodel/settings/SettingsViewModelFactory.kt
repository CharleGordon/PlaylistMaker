package com.example.playlistmaker.presentation.viewmodel.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.api.SharingInteractor
import com.example.domain.api.ThemeInteractor

class SettingsViewModelFactory(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsActivityViewModel::class.java)) {
            return SettingsActivityViewModel(
                sharingInteractor = sharingInteractor,
                themeInteractor = themeInteractor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}