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

    // Handling singleton within an abstract class instead of object (more Kotlin-like)
    // <https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7>
    // TODO (Clarify): Is this thread safe?
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.w(TAG, "Requesting AppDatabase instance")
                val instance = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "nym-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}