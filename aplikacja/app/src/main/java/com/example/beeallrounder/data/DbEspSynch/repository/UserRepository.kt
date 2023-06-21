package com.example.beeallrounder.data.DbEspSynch.repository

import com.example.beeallrounder.data.DbEspSynch.UserDao
import com.example.beeallrounder.data.DbEspSynch.model.SensorRecord

class UserRepository(private val userDao: UserDao) {
    suspend fun addSensorRecord(sensorRecord: SensorRecord) {
        userDao.addSensorRecord(sensorRecord)
    }
}