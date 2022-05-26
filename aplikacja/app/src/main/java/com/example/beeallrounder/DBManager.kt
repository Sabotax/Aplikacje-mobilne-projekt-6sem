package com.example.beeallrounder

import android.util.Log

object DBManager {
    // dedykowane słowo kluczowe do klasy Singleton
    fun addRecord(s:String) : Boolean {
        Log.d("Test add record", s)
        return true
    }
}