package com.kaeonx.nymandroidport.database

import androidx.room.Entity

// contactNymId is contact of ownerNymId
@Entity(primaryKeys = ["ownerAddress", "contactAddress"])
data class Contact(
    val ownerAddress: String,
    val contactAddress: String
)