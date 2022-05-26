package com.example.beeallrounder.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecord(snapshot:Beehive_snapshot)

    @Query("SELECT * FROM Beehive_snapshot ORDER BY id ASC")
    fun readAllData() : LiveData<List<Beehive_snapshot>>
}