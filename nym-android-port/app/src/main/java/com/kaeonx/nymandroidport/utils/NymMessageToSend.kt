package com.kaeonx.nymandroidport.utils

import androidx.annotation.Keep
import com.kaeonx.nymandroidport.database.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@kotlinx.serialization.Serializable
@Keep
@Suppress("unused")
internal class NymMessageToSend private constructor(
    internal val type: String,
    internal val message: String,
    internal val recipient: String
) {
    internal fun encodeToString(): String {
        return Json.encodeToString(this)
    }

    internal companion object {
        internal fun from(message: Message) = NymMessageToSend(
            type = "send",
            message = "${message.fromAddress}|${message.message}",
            recipient = message.toAddress
        )
    }
}