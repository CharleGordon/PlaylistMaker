package com.example.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY insertionTime DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getFavoriteTrackIds(): List<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_tracks_table WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Int): Boolean
}