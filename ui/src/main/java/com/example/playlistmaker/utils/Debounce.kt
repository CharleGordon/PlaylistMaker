package com.example.playlistmaker.utils

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debounce {

    private var clickJob: Job? = null

    fun clickDebounce(): Boolean {
        val current = clickJob
        if (current == null || !current.isActive) {
            clickJob = GlobalScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
            }
            return true
        }
        return false
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}