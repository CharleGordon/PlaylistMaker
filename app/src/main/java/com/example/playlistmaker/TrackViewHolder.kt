package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.textview.MaterialTextView

class TrackViewHolder (parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
){

    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val trackName : MaterialTextView = itemView.findViewById(R.id.trackName)
    private val artistName: MaterialTextView = itemView.findViewById(R.id.artistName)
    private val trackLenght: MaterialTextView = itemView.findViewById(R.id.trackLenght)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackLenght.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
    }

}