package com.example.beeallrounder.databases.dbEspSynch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.example.beeallrounder.databases.dbEspSynch.UserDatabase
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord
import com.example.beeallrounder.databases.dbEspSynch.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val repository: UserRepository
    val database: UserDatabase

    init {
        database = UserDatabase.getDatabase(application)
        val userDao = database.userDao()
        repository = UserRepository(userDao)
    }

    fun addSensorRecord(sensorRecord: SensorRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSensorRecord(sensorRecord)
        }
    }

    fun readBetween(espId: String,timeFrom: Long, timeTo: Long): LiveData<List<SensorRecord>> {
        return repository.readBetween(espId,timeFrom,timeTo)
    }

    fun getEspList(): LiveData<List<String>> {
        return repository.getEspList()
    }

    fun insertIfNotExist(sensorRecord: SensorRecord) {
        val query = """
           INSERT INTO SensorRecord (espId, waga, timestampEsp)
           VALUES (${sensorRecord.id}, ${sensorRecord.waga}, ${sensorRecord.timestampEsp})
           WHERE NOT EXISTS ( SELECT * FROM SensorRecord 
               WHERE waga = ${sensorRecord.waga}
               AND timestampEsp = ${sensorRecord.timestampEsp}
           );
        """.trimIndent()
        executeSQL(query)
    }

    private fun executeSQL(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            database.openHelper.writableDatabase.execSQL(query)
        }
    }
}