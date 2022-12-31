package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kaeonx.nymandroidport.ui.screens.clientinfo.SELECTED_CLIENT_ADDRESS_KSVP_KEY
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {
    // Sub-query returns null if no value is found. Null is not equal to anything.
    @Query(
        "SELECT * FROM contact WHERE ownerAddress = (SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey);"
    )
    fun getBySelectedClient(selectedClientAddressKey: String = SELECTED_CLIENT_ADDRESS_KSVP_KEY): Flow<List<Contact>>  // empty list if no client is selected

    @Query(
        "INSERT INTO contact VALUES ((SELECT `value` FROM keystringvaluepair WHERE `key` = :selectedClientAddressKey), :newContactAddress);"
    )
    suspend fun insertForSelectedClient(
        newContactAddress: String,
        selectedClientAddressKey: String = SELECTED_CLIENT_ADDRESS_KSVP_KEY
    ): Long

    // Returns new rowId
    @Insert
    suspend fun insert(contact: Contact): Long

    // Returns number of rows successfully deleted
    @Delete
    suspend fun delete(contact: Contact): Int
}