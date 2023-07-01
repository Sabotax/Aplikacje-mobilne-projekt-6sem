package com.example.beeallrounder.databases.dbEspSynch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.beeallrounder.databases.dbEspSynch.UserDatabase
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord
import com.example.beeallrounder.databases.dbEspSynch.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addSensorRecord(sensorRecord: SensorRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSensorRecord(sensorRecord)
        }
    }

    fun readBetween(timeFrom: Long, timeTo: Long): LiveData<List<SensorRecord>> {
        return repository.readBetween(timeFrom,timeTo)
    }
}