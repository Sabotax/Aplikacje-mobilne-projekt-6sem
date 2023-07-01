package com.example.beeallrounder.databases.dbEspSynch

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot

@Dao
interface UserDao {

    @Insert
    suspend fun addSensorRecord(sensorRecord: SensorRecord)

    @Query("SELECT * FROM SensorRecord WHERE timestampEsp BETWEEN :timeFrom AND :timeTo ORDER BY timestampEsp ASC")
    fun readBetween(timeFrom: Long, timeTo: Long) : LiveData<List<SensorRecord>>
}