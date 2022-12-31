package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyStringValuePairDAO {
    @Query("SELECT * FROM keystringvaluepair WHERE `key` = :key;")
    fun get(key: String): Flow<KeyStringValuePair?>  // null returned if key isn't found in the table

    @Query("SELECT * FROM keystringvaluepair WHERE `key` in(:keys);")
    fun get(keys: List<String>): Flow<List<KeyStringValuePair>>

    // Upsert operation, replace if present
    /**
     * This is always performed as a transaction, in the case that `keyStringValuePairs` contains
     * more than 1 item.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(keyStringValuePairs: List<KeyStringValuePair>)

    // Returns number of rows successfully deleted
    @Query("DELETE FROM keystringvaluepair WHERE `key` = :key;")
    suspend fun delete(key: String): Int

    // Returns number of rows successfully deleted
    @Query("DELETE FROM keystringvaluepair WHERE `key` IN(:keys);")
    suspend fun delete(keys: List<String>): Int
}