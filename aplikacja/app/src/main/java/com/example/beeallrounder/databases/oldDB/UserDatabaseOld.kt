package com.example.beeallrounder.databases.oldDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beeallrounder.databases.oldDB.model.Beehive_snapshot

@Database(entities = [Beehive_snapshot::class], version = 1, exportSchema = false)
abstract class UserDatabaseOld : RoomDatabase() {

    abstract fun userDaoOld() : UserDaoOld

    companion object {
        @Volatile
        private var INSTANCE : UserDatabaseOld? = null

        fun getDatabase(context: Context) : UserDatabaseOld {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabaseOld::class.java,
                    "database"
                ).build()
                INSTANCE =instance
                return instance
            }
        }
    }
}