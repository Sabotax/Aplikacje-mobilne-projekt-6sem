package com.example.beeallrounder.data.oldDB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Beehive_snapshot")
data class Beehive_snapshot  (
    @PrimaryKey(autoGenerate = true) val id : Int,
    val date : String,
    val hiveNumber: Int,
    val notes: String
): Parcelable
