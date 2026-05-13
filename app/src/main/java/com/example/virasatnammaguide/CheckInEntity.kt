package com.example.virasatnammaguide

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_ins")
data class CheckInEntity(
    @PrimaryKey val siteId: String,
    val checkedInAt: Long
)
