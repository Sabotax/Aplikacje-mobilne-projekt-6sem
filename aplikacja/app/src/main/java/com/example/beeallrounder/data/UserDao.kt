package com.example.beeallrounder.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.beeallrounder.data.model.Beehive_snapshot

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(snapshot: Beehive_snapshot)

    @Query("SELECT * FROM Beehive_snapshot ORDER BY id ASC")
    fun readAllData() : LiveData<List<Beehive_snapshot>>

    @Update
    suspend fun updateRecord(beehiveSnapshot: Beehive_snapshot)
}