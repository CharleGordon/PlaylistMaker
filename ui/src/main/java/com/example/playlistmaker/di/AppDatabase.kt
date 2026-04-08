package com.example.playlistmaker.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.db.dao.FavoriteTracksDao
import com.example.data.db.dao.PlaylistDao
import com.example.data.db.dao.TrackInPlaylistDao
import com.example.data.db.entity.PlaylistEntity
import com.example.data.db.entity.TrackEntity
import com.example.data.db.entity.TrackInPlaylistEntity

@Database(entities = [TrackEntity::class,
    PlaylistEntity::class,
    TrackInPlaylistEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

}