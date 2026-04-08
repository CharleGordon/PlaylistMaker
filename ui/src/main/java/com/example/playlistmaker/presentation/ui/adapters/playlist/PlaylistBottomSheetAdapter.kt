package com.example.playlistmaker.presentation.ui.adapters.playlist

import android.view.ViewGroup
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.domain.models.Playlist
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistHorizontalBinding

class PlaylistBottomSheetAdapter(private val clickListener: (Playlist) -> Unit) :
    ListAdapter<Playlist, PlaylistBottomSheetAdapter.ViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener { clickListener(playlist) }
    }

    class ViewHolder(private val binding: ItemPlaylistHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val cornerRadiusInPixels = itemView.resources.getDimensionPixelSize(R.dimen.track_image_corner_radius)

        fun bind(playlist: Playlist) {
            binding.trackName.text = playlist.title
            binding.tracksCount.text = itemView.context.resources.getQuantityString(
                R.plurals.tracks_count, playlist.tracksCount, playlist.tracksCount
            )
            Glide.with(itemView)
                .load(playlist.imagePath)
                .placeholder(R.drawable.mini_placeholder)
                .transform(RoundedCorners(cornerRadiusInPixels))
                .centerCrop()
                .into(binding.trackImage)
        }
    }
}