package com.example.beeallrounder.databases.dbEspSynch

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord

@Dao
interface UserDao {

    @Insert
    suspend fun addSensorRecord(sensorRecord: SensorRecord)

    @Query("SELECT * FROM SensorRecord WHERE timestampEsp BETWEEN :timeFrom AND :timeTo AND espId = :espId ORDER BY timestampEsp ASC")
    fun readBetween(espId: String,timeFrom: Long, timeTo: Long) : LiveData<List<SensorRecord>>

    @Query("SELECT DISTINCT espId from SensorRecord")
    fun getEspList(): LiveData<List<String>>
}