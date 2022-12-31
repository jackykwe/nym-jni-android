package com.kaeonx.nymandroidport.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val TAG = "appDatabase"

@Database(
    entities = [
        Contact::class,
        KeyStringValuePair::class,
        Message::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDAO
    abstract fun keyStringValuePairDao(): KeyStringValuePairDAO
    abstract fun messageDao(): MessageDAO

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(applicationContext: Context): AppDatabase {
            // TODO: Not thread safe?
            if (instance == null) {
                Log.w(TAG, "Requesting AppDatabase instance")
                instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "nym-db"
                ).build()
            }
            return instance!!
        }
    }
}