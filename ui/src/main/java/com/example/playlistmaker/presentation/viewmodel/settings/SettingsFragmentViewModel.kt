package com.example.playlistmaker.presentation.viewmodel.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.api.ThemeInteractor
import com.example.domain.api.SharingInteractor
import com.example.domain.models.ThemeSettings

class SettingsFragmentViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor
) : ViewModel() {

    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    val themeSettings: LiveData<ThemeSettings> = themeSettingsLiveData

    init {
        val currentThemeSettings = themeInteractor.getThemeSettings()
        themeSettingsLiveData.postValue(currentThemeSettings)
    }

    fun onThemeSwitch(isDark: Boolean) {
        val newSettings = ThemeSettings(isDarkTheme = isDark)
        themeInteractor.updateThemeSetting(newSettings)
        themeSettingsLiveData.postValue(newSettings)
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onTermsClicked() {
        sharingInteractor.openTerms()
    }

}