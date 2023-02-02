package com.kaeonx.nymandroidport.utils

internal class NymBinaryMessageReceived private constructor(internal val message: String) {
    internal val senderAddress
        get() = message.substringBefore('|')
    internal val trueMessage
        get() = message.substringAfter('|')

    companion object {
        internal fun from(message: String) = NymBinaryMessageReceived(message = message)
    }
}