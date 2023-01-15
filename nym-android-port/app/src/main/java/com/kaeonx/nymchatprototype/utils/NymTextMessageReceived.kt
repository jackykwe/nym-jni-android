package com.kaeonx.nymchatprototype.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Suppress("unused")
@kotlinx.serialization.Serializable
internal class NymTextMessageReceived private constructor(
    internal val type: String,
    internal val message: String,
    internal val senderTag: String?
) {
    internal val senderAddress
        get() = message.substringBefore('|')
    internal val trueMessage
        get() = message.substringAfter('|')

    companion object {
        internal fun from(rawJson: String): NymTextMessageReceived = Json.decodeFromString(rawJson)
    }
}