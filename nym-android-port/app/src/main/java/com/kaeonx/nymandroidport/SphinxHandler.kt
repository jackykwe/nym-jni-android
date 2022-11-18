package com.kaeonx.nymandroidport

internal fun generatePseudorandomBytes(key: ByteArray, iv: ByteArray, length: Int): ByteArray =
    generatePseudorandomBytesImpl(key, iv, length)
private external fun generatePseudorandomBytesImpl(key: ByteArray, iv: ByteArray, length: Int): ByteArray