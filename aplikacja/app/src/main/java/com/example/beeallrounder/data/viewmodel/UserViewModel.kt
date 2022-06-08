package com.example.beeallrounder.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.beeallrounder.data.UserDatabase
import com.example.beeallrounder.data.repository.UserRepository
import com.example.beeallrounder.data.model.Beehive_snapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData : LiveData<List<Beehive_snapshot>>
    private val repository: UserRepository

    init {
        val userDao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        readAllData = repository.readAllData
    }

    fun addBeehive(snapshot: Beehive_snapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBeehive(snapshot)
        }
    }
}