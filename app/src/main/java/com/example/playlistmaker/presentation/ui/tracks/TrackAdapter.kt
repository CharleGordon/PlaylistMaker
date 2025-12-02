package com.example.playlistmaker.presentation.ui.tracks

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val tracks: List<Track>,
    private val clickListener: TrackClickListener
) : RecyclerView.Adapter<TrackViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {
            clickListener.onTrackClickListener(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun interface TrackClickListener {
        fun onTrackClickListener(track: Track)
    }
}