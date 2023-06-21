package com.example.beeallrounder.databases.dbEspSynch

import androidx.room.Dao
import androidx.room.Insert
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord

@Dao
interface UserDao {

    @Insert
    suspend fun addSensorRecord(sensorRecord: SensorRecord)
}