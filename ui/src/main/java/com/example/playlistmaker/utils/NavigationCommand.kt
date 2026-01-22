package com.example.playlistmaker.utils

sealed class NavigationCommand {
    object GoToSearch : NavigationCommand()
    object GoToMedia : NavigationCommand()
    object GoToSettings : NavigationCommand()
}