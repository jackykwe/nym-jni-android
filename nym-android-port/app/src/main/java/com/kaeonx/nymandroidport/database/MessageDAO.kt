package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDAO {
    // TODO: Use paging
    // DONE: Consider having 1 database per user, right now all users share 1 database (need to see
    // this when hooking up database singleton). There's no need for this: access to database is
    // handled by Kotlin code. The end user cannot normally access the database directly.
    @Query(
        "SELECT * FROM message " +
                "WHERE (fromAddress = :contactAddress AND toAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey)) " +
                "OR (fromAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey) AND toAddress = :contactAddress) " +
                "ORDER BY id;"
    )
    fun getAllWithSelectedClient(
        contactAddress: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ): Flow<List<Message>>

    // Returns new rowId
    @Query(
        "INSERT INTO message (fromAddress, toAddress, message) VALUES ((SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :toAddress, :message);"
    )
    suspend fun insertFromSelectedClient(
        toAddress: String,
        message: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ): Long

    // Returns new rowId
    @Query(
        "INSERT INTO message (fromAddress, toAddress, message) VALUES (:fromAddress, (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :message);"
    )
    suspend fun debugInsertToSelectedClient(
        fromAddress: String,
        message: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ): Long

    // Returns number of rows successfully deleted
    @Delete
    suspend fun delete(message: Message): Int
}