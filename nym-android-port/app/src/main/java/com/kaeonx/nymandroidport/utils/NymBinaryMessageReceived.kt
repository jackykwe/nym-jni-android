package com.kaeonx.nymandroidport.utils

import androidx.annotation.Keep

@Keep  // Just in case a binary message is received from websocket
internal class NymBinaryMessageReceived private constructor(internal val message: String) {
    internal val senderAddress
        get() = message.substringBefore('|')
    internal val trueMessage
        get() = message.substringAfter('|')

    companion object {
        internal fun from(message: String) = NymBinaryMessageReceived(message = message)
    }
}