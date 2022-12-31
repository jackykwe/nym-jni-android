package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDAO {
    // TODO: Use paging
    // TODO: Have 1 database per user, right now all users share 1 database (need to see this when hooking up database singleton)  // NO NEED: managed by Kotlin code
    /*
     * TODO: use distinctUntilChanged()
     * Observable queries in Room have one important limitation: the query reruns whenever any row
     * in the table is updated, whether or not that row is in the result set. You can ensure that
     * the UI is only notified when the actual query results change by applying the
     * distinctUntilChanged() operator at the observation site.
     */
    @Query("SELECT * FROM message WHERE fromAddress = :senderNymId AND toAddress = :receiverNymId")
    fun getAllBySender(senderNymId: String, receiverNymId: String): Flow<List<Message>>

    // Returns new rowId
    @Insert
    suspend fun insert(message: Message): Long

    // Returns number of rows successfully deleted
    @Delete
    suspend fun delete(message: Message): Int
}