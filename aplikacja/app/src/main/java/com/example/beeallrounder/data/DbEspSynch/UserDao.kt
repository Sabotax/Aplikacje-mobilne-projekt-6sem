package com.example.beeallrounder.data.DbEspSynch

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.beeallrounder.data.DbEspSynch.model.SensorRecord

@Dao
interface UserDao {

    @Insert
    suspend fun addSensorRecord(sensorRecord: SensorRecord)
}