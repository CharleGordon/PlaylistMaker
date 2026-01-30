package com.example.playlistmaker.presentation.ui.adapters.fragmentAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.presentation.ui.media.FavoriteTracksFragment
import com.example.playlistmaker.presentation.ui.media.PlaylistFragment

class MediaViewPagerAdapter(
    fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoriteTracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}