package com.kaeonx.nymandroidport.utils

import androidx.annotation.Keep
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
@Keep
@Suppress("unused")
internal class NymTextMessageReceived private constructor(
    internal val type: String,
    internal val message: String,
    internal val senderTag: String? = null  // having default value means optional when decodeFromString() is executed
) {
    internal val senderAddress
        get() = message.substringBefore('|')
    internal val trueMessage
        get() = message.substringAfter('|')

    companion object {
        internal fun from(rawJson: String): NymTextMessageReceived = Json.decodeFromString(rawJson)
    }
}