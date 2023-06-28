package com.example.beeallrounder.databases.dbEspSynch.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "SensorRecord")
data class SensorRecord (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    //@ForeignKey() val id_sensorDay: Int,
    val espId: String,
    val waga: Double?,
    val timestampEsp: Long?,
    val timestampTel: Long = System.currentTimeMillis(),
    val synchedToServer: Boolean = false
): Parcelable {

}