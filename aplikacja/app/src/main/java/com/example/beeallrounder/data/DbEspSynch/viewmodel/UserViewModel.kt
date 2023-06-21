package com.example.beeallrounder.data.DbEspSynch.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beeallrounder.data.DbEspSynch.UserDao
import com.example.beeallrounder.data.DbEspSynch.UserDatabase
import com.example.beeallrounder.data.DbEspSynch.model.SensorRecord
import com.example.beeallrounder.data.DbEspSynch.repository.UserRepository
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
}