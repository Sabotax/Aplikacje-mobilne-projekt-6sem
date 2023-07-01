package com.example.beeallrounder.databases.dbEspSynch.repository

import androidx.lifecycle.LiveData
import com.example.beeallrounder.databases.dbEspSynch.UserDao
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord

class UserRepository(private val userDao: UserDao) {
    suspend fun addSensorRecord(sensorRecord: SensorRecord) {
        userDao.addSensorRecord(sensorRecord)
    }

    fun readBetween(timeFrom: Long, timeTo: Long) : LiveData<List<SensorRecord>> {
        return userDao.readBetween(timeFrom,timeTo)
    }
}