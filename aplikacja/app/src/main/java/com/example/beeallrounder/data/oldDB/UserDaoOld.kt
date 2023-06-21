package com.example.beeallrounder.data.oldDB

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.beeallrounder.data.oldDB.model.Beehive_snapshot

@Dao
interface UserDaoOld {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(snapshot: Beehive_snapshot)

    @Query("SELECT * FROM Beehive_snapshot ORDER BY id ASC")
    fun readAllData() : LiveData<List<Beehive_snapshot>>

    @Update
    suspend fun updateRecord(beehiveSnapshot: Beehive_snapshot)

    @Delete
    suspend fun deleteRecord(beehiveSnapshot: Beehive_snapshot)

    @Query("DELETE FROM Beehive_snapshot")
    suspend fun deleteAllRecords()
}