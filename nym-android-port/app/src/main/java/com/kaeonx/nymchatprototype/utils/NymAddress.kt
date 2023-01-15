package com.kaeonx.nymchatprototype.utils

internal data class NymAddress(
    internal val userIdentityKey: String,
    internal val userEncryptionKey: String,
    internal val gatewayIdentityKey: String
) {
    companion object {
        internal fun from(address: String): NymAddress {
            return NymAddress(
                userIdentityKey = address.substringBefore('.'),
                userEncryptionKey = address.substringAfter('.').substringBefore('@'),
                gatewayIdentityKey = address.substringAfter('@')
            )
        }
    }
}