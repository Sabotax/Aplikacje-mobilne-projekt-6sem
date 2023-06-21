package com.example.beeallrounder.data.oldDB.repository

import androidx.lifecycle.LiveData
import com.example.beeallrounder.data.oldDB.UserDaoOld
import com.example.beeallrounder.data.oldDB.model.Beehive_snapshot

class UserRepositoryOld(private val userDaoOld: UserDaoOld) {

    val readAllData : LiveData<List<Beehive_snapshot>> = userDaoOld.readAllData()

    suspend fun addBeehive(snapshot: Beehive_snapshot) {
        userDaoOld.addRecord(snapshot)
    }

    suspend fun updateRecord(beehiveSnapshot: Beehive_snapshot) {
        userDaoOld.updateRecord(beehiveSnapshot)
    }

    suspend fun deleteRecord(beehiveSnapshot: Beehive_snapshot) {
        userDaoOld.deleteRecord(beehiveSnapshot)
    }

    suspend fun deleteAllRecord() {
        userDaoOld.deleteAllRecords()
    }
}