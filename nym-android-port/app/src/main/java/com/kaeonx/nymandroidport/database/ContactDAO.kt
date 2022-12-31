package com.kaeonx.nymandroidport.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDAO {
    @Query("SELECT * FROM contact WHERE ownerAddress = :selectedClientAddress")
    fun getAll(selectedClientAddress: String): Flow<List<Contact>>

    // Returns new rowId
    @Insert
    suspend fun insert(contact: Contact): Long

    // Returns number of rows successfully deleted
    @Delete
    suspend fun delete(contact: Contact): Int
}