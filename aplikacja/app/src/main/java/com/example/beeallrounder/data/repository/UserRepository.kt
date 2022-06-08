package com.example.beeallrounder.data.repository

import androidx.lifecycle.LiveData
import com.example.beeallrounder.data.UserDao
import com.example.beeallrounder.data.model.Beehive_snapshot

class UserRepository(private val userDao: UserDao) {

    val readAllData : LiveData<List<Beehive_snapshot>> = userDao.readAllData()

    suspend fun addBeehive(snapshot: Beehive_snapshot) {
        userDao.addRecord(snapshot)
    }

    suspend fun updateRecord(beehiveSnapshot: Beehive_snapshot) {
        userDao.updateRecord(beehiveSnapshot)
    }
}