package com.kaeonx.nymchatprototype.utils

import com.kaeonx.nymandroidport.utils.NymAddress

internal data class NymAddress(
    internal val userIdentityKey: String,
    internal val userEncryptionKey: String,
    internal val gatewayIdentityKey: String
) {
    companion object {
        internal fun from(address: String): NymAddress {
            return com.kaeonx.nymandroidport.utils.NymAddress(
                userIdentityKey = address.substringBefore('.'),
                userEncryptionKey = address.substringAfter('.').substringBefore('@'),
                gatewayIdentityKey = address.substringAfter('@')
            )
        }
    }
}