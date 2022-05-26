package com.example.beeallrounder.data

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {

    val readAllData : LiveData<List<Beehive_snapshot>> = userDao.readAllData()

    suspend fun addBeehive(snapshot: Beehive_snapshot) {
        userDao.addRecord(snapshot)
    }
}