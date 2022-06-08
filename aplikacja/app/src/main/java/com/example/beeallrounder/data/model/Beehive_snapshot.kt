package com.example.beeallrounder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "Beehive_snapshot")
data class Beehive_snapshot (
    @PrimaryKey(autoGenerate = true) val id : Int,
    val date : String,
    val hiveNumber: Int,
    val notes: String
)
