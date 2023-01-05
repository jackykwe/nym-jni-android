package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {
    // Sub-query returns null if no value is found. Null is not equal to anything.
    @Query(
        "SELECT * FROM contact WHERE ownerAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey);"
    )
    fun getAllBySelectedClient(selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY): Flow<List<Contact>>  // empty list if no client is selected

    @Query(
        "INSERT INTO contact VALUES ((SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :newContactAddress);"
    )
    suspend fun insertForSelectedClient(
        newContactAddress: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY
    )

    // Returns number of rows successfully deleted
    @Query(
        "DELETE FROM contact WHERE ownerAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey) AND contactAddress = :contactAddress"
    )
    suspend fun deleteForSelectedClient(
        contactAddress: String,
        selectedClientAddressKey: String = RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ): Int
}