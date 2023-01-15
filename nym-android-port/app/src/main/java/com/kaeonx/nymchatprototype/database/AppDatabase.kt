package com.kaeonx.nymchatprototype.database

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
    // Clarified: Is this thread safe? Yes. Note the need to do double null check. Also,
    // optimisation has been done: double-checked locking.
    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase {
            return instance ?: synchronized(this) {
                if (instance == null) {  // double null check necessary!
                    Log.w(TAG, "Requesting AppDatabase instance")
                    instance = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "nym-db"
                    ).enableMultiInstanceInvalidation()
                        .build()
                }
                instance!!
            }
        }
    }
}