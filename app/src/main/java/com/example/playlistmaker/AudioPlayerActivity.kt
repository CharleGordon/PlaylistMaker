package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var cancelView: MaterialToolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackNameView: MaterialTextView
    private lateinit var artistNameView: MaterialTextView
    private lateinit var trackTimeView: MaterialTextView
    private lateinit var collectionNameView: MaterialTextView
    private lateinit var releaseYearView: MaterialTextView
    private lateinit var genreView: MaterialTextView
    private lateinit var countryView: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player_view)

        initsViews()

        cancelView.setNavigationOnClickListener {
            finish()
        }

        val track = getTrackFromIntent()

        if (track != null) {
            fillPlayerData(track)
        } else {

        }
    }

    private fun initsViews() {

        cancelView = findViewById(R.id.topPanel)
        trackImage = findViewById(R.id.audioPlayerTrackImage)
        trackNameView = findViewById(R.id.trackNameBelowImage)
        artistNameView = findViewById(R.id.artistNameBelowImage)
        trackTimeView = findViewById(R.id.trackLength)
        collectionNameView = findViewById(R.id.collectionName)
        releaseYearView = findViewById(R.id.releaseYear)
        genreView = findViewById(R.id.primaryGenreName)
        countryView = findViewById(R.id.country)
    }

    private fun getTrackFromIntent(): Track? {

        val trackJson = intent.getStringExtra(TRACK_KEY)

        return if(trackJson != null) {
            val gson = Gson()
            gson.fromJson(trackJson, Track::class.java)
        } else {
            null
        }
    }

    private fun fillPlayerData(track: Track) {

        val coverArtwork = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
        Glide.with(this)
            .load(coverArtwork)
            .placeholder(R.drawable.placeholder_image512x512)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.margin_8dp)))
            .into(trackImage)

        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        val albumGroup = findViewById<androidx.constraintlayout.widget.Group>(R.id.albumGroup)
        val yearGroup = findViewById<androidx.constraintlayout.widget.Group>(R.id.yearGroup)


        if (track.collectionName != null) {
            collectionNameView.text = track.collectionName
            albumGroup.visibility = View.VISIBLE
        } else {
            albumGroup.visibility = View.GONE
        }

        if (track.releaseDate != null) {
            releaseYearView.text = track.releaseDate.substring(0, 4)
            yearGroup.visibility = View.VISIBLE
        } else {
            yearGroup.visibility = View.GONE
        }

        genreView.text = track.primaryGenreName
        countryView.text = track.country
    }

    companion object {
        const val TRACK_KEY = "track_data"
    }
}