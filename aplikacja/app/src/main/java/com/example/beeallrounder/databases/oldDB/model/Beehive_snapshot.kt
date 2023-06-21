package com.example.beeallrounder.databases.oldDB.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Beehive_snapshot")
data class Beehive_snapshot  (
    @PrimaryKey(autoGenerate = true) val id : Int,
    val date : String,
    val hiveNumber: Int,
    val notes: String
): Parcelable
