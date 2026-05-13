package com.example.virasatnammaguide

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins ORDER BY checkedInAt DESC")
    suspend fun getAll(): List<CheckInEntity>

    @Query("SELECT * FROM check_ins WHERE siteId = :siteId LIMIT 1")
    suspend fun getBySiteId(siteId: String): CheckInEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(checkIn: CheckInEntity)
}
