package com.example.playlistmaker.presentation.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.example.playlistmaker.utils.SingleLiveEvent
import com.example.playlistmaker.utils.NavigationCommand

class MainActivityViewModel: ViewModel() {

    private val navigationCommandSingleEvent = SingleLiveEvent<NavigationCommand>()
    val navigationCommand: LiveData<NavigationCommand> = navigationCommandSingleEvent

    fun onSearchClicked() {
        navigationCommandSingleEvent.value = NavigationCommand.GoToSearch
    }

    fun onMediaClicked() {
        navigationCommandSingleEvent.value = NavigationCommand.GoToMedia
    }

    fun onSettingsClicked() {
        navigationCommandSingleEvent.value = NavigationCommand.GoToSettings
    }
}