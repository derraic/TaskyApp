package com.derra.taskyapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deletes")
data class DeleteEntity(
    val type: String,
    @PrimaryKey val id: String
)
