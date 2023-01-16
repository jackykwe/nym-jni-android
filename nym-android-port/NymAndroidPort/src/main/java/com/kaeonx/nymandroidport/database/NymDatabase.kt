package com.kaeonx.nymandroidport.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val TAG = "appDatabase"

@Database(
    entities = [
        KeyStringValuePair::class,
        NymEnqueuedOutgoingTextMessage::class,
    ],
    version = 1
)
abstract class NymDatabase : RoomDatabase() {
    abstract fun keyStringValuePairDao(): KeyStringValuePairDAO
    abstract fun nymTextMessageToSendDao(): NymEnqueuedOutgoingTextMessageDAO

    // Handling singleton within an abstract class instead of object (more Kotlin-like)
    // <https://developer.android.com/codelabs/android-room-with-a-view-kotlin#7>
    // Clarified: Is this thread safe? Yes. Note the need to do double null check. Also,
    // optimisation has been done: double-checked locking.
    companion object {
        @Volatile
        private var instance: NymDatabase? = null

        fun getInstance(applicationContext: Context): NymDatabase {
            return instance ?: synchronized(this) {
                if (instance == null) {  // double null check necessary!
                    Log.w(TAG, "Requesting NymDatabase instance")
                    instance = Room.databaseBuilder(
                        applicationContext,
                        NymDatabase::class.java,
                        "nym-android-port-db"
                    ).enableMultiInstanceInvalidation()
                        .build()
                }
                instance!!
            }
        }
    }
}