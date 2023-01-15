package com.kaeonx.nymchatprototype.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// KSVP is KeyStringValuePair
internal const val RUNNING_CLIENT_ID_KSVP_KEY = "runningClientId"
internal const val RUNNING_CLIENT_ADDRESS_KSVP_KEY = "runningClientAddress"
internal const val NYM_RUN_STATE_KSVP_KEY = "nymRunState"

@Entity
data class KeyStringValuePair(
    @PrimaryKey val key: String,
    val value: String
)