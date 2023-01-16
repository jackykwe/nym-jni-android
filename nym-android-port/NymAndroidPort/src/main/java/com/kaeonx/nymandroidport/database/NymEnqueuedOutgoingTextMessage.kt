package com.kaeonx.nymandroidport.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Transient

@Entity
@kotlinx.serialization.Serializable
class NymEnqueuedOutgoingTextMessage private constructor(
    @Transient @PrimaryKey(autoGenerate = true) internal val id: Int = 0,  // not included in serialisation
    internal val type: String,
    internal val message: String,
    internal val recipient: String,
    @Transient internal val sent: Boolean = false,  // not included in serialisation
) {
    internal fun encodeToString(): String {
        return Json.encodeToString(this)
    }
}