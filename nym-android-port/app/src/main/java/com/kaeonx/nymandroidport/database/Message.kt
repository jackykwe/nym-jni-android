package com.kaeonx.nymandroidport.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fromAddress: String,
    val toAddress: String,
    val message: String
)