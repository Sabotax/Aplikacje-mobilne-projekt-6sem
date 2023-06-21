package com.example.beeallrounder.databases.dbEspSynch

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beeallrounder.databases.dbEspSynch.model.SensorRecord

@Database(entities = [SensorRecord::class], version = 1, exportSchema = false)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object {
        @Volatile
        private var INSTANCE : UserDatabase? = null

        fun getDatabase(context: Context) : UserDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "database"
                ).build()
                INSTANCE =instance
                return instance
            }
        }
    }
}