package com.example.playlistmaker.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.db.dao.FavoriteTracksDao
import com.example.data.db.entity.TrackEntity

@Database(entities = [TrackEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
}