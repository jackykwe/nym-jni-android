package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NymEnqueuedOutgoingTextMessageDAO {
    @Query(
        "SELECT * FROM message " +
                "WHERE fromAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey) AND sent = :sent " +
                "ORDER BY id LIMIT 1;"
    )
    fun getEarliestPendingSendFromSelectedClient(
        sent: Boolean = false,  // no reason to override
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY  // no reason to override
    ): Flow<NymEnqueuedOutgoingTextMessage?>

    @Query(
        "UPDATE message SET sent = :sent WHERE id = :id;"
    )
    suspend fun updateEarliestPendingSendById(id: Int, sent: Boolean = true)

    // Returns new rowId
    @Query(
        "INSERT INTO message (fromAddress, toAddress, message, sent) VALUES ((SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :toAddress, :message, :sent);"
    )
    suspend fun insertFromSelectedClient(
        toAddress: String,
        message: String,
        sent: Boolean = false,  // no reason to override
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY  // no reason to override
    ): Long

    // Returns new rowId
    @Query(
        "INSERT INTO message (fromAddress, toAddress, message, sent) VALUES (:fromAddress, (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :message, :sent);"
    )
    suspend fun insertToSelectedClient(
        fromAddress: String,
        message: String,
        sent: Boolean = true,  // no reason to override
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY  // no reason to override
    ): Long

    // Returns number of rows successfully deleted
    @Query(
        "DELETE FROM message " +
                "WHERE (fromAddress = :contactAddress AND toAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey)) " +
                "OR (fromAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey) AND toAddress = :contactAddress);"
    )
    suspend fun deleteBetweenSelectedClientAndContact(
        contactAddress: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY  // no reason to override
    ): Int
}