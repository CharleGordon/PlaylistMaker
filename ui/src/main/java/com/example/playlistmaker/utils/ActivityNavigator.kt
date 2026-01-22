package com.example.playlistmaker.utils

import android.content.Context
import android.content.Intent
import com.example.domain.models.Track
import com.example.playlistmaker.presentation.ui.player.AudioPlayerActivity
import com.google.gson.Gson

class ActivityNavigator(private val context: Context) {

    fun openTrackPlayer(track: Track) {
        val playerIntent = Intent(context, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.TRACK_KEY, Gson().toJson(track))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(playerIntent)
    }
}