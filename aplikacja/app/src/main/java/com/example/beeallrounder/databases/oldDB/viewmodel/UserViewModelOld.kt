package com.example.beeallrounder.databases.oldDB.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.beeallrounder.databases.oldDB.UserDatabaseOld
import com.example.beeallrounder.databases.oldDB.repository.UserRepositoryOld
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModelOld(application: Application) : AndroidViewModel(application) {

    val readAllData : LiveData<List<Beehive_snapshot>>
    private val repository: UserRepositoryOld

    init {
        val userDao = UserDatabaseOld.getDatabase(application).userDaoOld()
        repository = UserRepositoryOld(userDao)
        readAllData = repository.readAllData
    }

    fun addBeehive(snapshot: Beehive_snapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBeehive(snapshot)
        }
    }

    fun updateRecord(beehiveSnapshot: Beehive_snapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecord(beehiveSnapshot)
        }
    }

    fun deleteRecord(beehiveSnapshot: Beehive_snapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteRecord(beehiveSnapshot)
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllRecord()
        }
    }
}