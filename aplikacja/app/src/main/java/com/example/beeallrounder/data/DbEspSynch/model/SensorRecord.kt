package com.example.beeallrounder.data.DbEspSynch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
@Entity(tableName = "SensorRecord")
data class SensorRecord (
    @PrimaryKey(autoGenerate = true) val id : Int,
    //@ForeignKey() val id_sensorDay: Int,
    val espId: String,
    val waga: Double,
    val timestamp: Timestamp,
    val synchedToServer: Boolean = false
): Parcelable