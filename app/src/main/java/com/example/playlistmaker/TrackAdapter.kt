package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val tracks: List<Track>,
    private val onTrackClickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}