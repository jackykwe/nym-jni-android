package com.kaeonx.nymandroidport.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KeyStringValuePair(
    @PrimaryKey val key: String,
    val value: String
)