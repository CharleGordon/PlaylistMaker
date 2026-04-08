package com.example.playlistmaker.presentation.ui.adapters.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.domain.models.Playlist
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val clickListener: (Playlist) -> Unit) :
    ListAdapter<Playlist, PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(ItemPlaylistBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { clickListener(getItem(position)) }
    }
}

class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val cornerRadiusInPixels = itemView.resources.getDimensionPixelSize(R.dimen.playlist_image_corner_radius)

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.title

        val countText = binding.root.context.resources.getQuantityString(
            R.plurals.tracks_count, playlist.tracksCount, playlist.tracksCount
        )
        binding.tracksCount.text = countText

        Glide.with(itemView)
            .load(playlist.imagePath)
            .placeholder(R.drawable.mini_placeholder)
            .transform(RoundedCorners(cornerRadiusInPixels))
            .centerCrop()
            .into(binding.playlistImage)
    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}