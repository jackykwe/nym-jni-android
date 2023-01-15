@file:Suppress("FunctionName")

package com.kaeonx.nymchatprototype.jni.instrumentedtesthelpers

/**
 * Send a `Boolean` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendBooleanThenReceiveString(arg: Boolean): String =
    _testSendBooleanThenReceiveStringImpl(arg)

private external fun _testSendBooleanThenReceiveStringImpl(arg: Boolean): String

/**
 * Send a `Byte` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendByteThenReceiveString(arg: Byte): String =
    _testSendByteThenReceiveStringImpl(arg)

private external fun _testSendByteThenReceiveStringImpl(arg: Byte): String

/**
 * Send a `UByte` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendUByteThenReceiveString(arg: UByte): String =
    _testSendUByteThenReceiveStringImpl(arg)

private external fun _testSendUByteThenReceiveStringImpl(arg: UByte): String

/**
 * Send a `Short` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendShortThenReceiveString(arg: Short): String =
    _testSendShortThenReceiveStringImpl(arg)

private external fun _testSendShortThenReceiveStringImpl(arg: Short): String

/**
 * Send a `UShort` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendUShortThenReceiveString(arg: UShort): String =
    _testSendUShortThenReceiveStringImpl(arg)

private external fun _testSendUShortThenReceiveStringImpl(arg: UShort): String

/**
 * Send a `Int` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendIntThenReceiveString(arg: Int): String =
    _testSendIntThenReceiveStringImpl(arg)

private external fun _testSendIntThenReceiveStringImpl(arg: Int): String

/**
 * Send a `UInt` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendUIntThenReceiveString(arg: UInt): String =
    _testSendUIntThenReceiveStringImpl(arg)

private external fun _testSendUIntThenReceiveStringImpl(arg: UInt): String

/**
 * Send a `Long` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendLongThenReceiveString(arg: Long): String =
    _testSendLongThenReceiveStringImpl(arg)

private external fun _testSendLongThenReceiveStringImpl(arg: Long): String

/**
 * Send a `ULong` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendULongThenReceiveString(arg: ULong): String =
    _testSendULongThenReceiveStringImpl(arg)

private external fun _testSendULongThenReceiveStringImpl(arg: ULong): String

/**
 * Send a `Float` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendFloatThenReceiveString(arg: Float): String =
    _testSendFloatThenReceiveStringImpl(arg)

private external fun _testSendFloatThenReceiveStringImpl(arg: Float): String

/**
 * Send a `Double` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendDoubleThenReceiveString(arg: Double): String =
    _testSendDoubleThenReceiveStringImpl(arg)

private external fun _testSendDoubleThenReceiveStringImpl(arg: Double): String

/**
 * Send a `String` from Kotlin to Rust, then receive back a `String` reporting what Rust received,
 * after Rust performs the uppercase transform on the provided `String`.
 */
internal fun _testSendStringThenUppercaseThenReceiveString(arg: String): String =
    _testSendStringThenUppercaseThenReceiveStringImpl(arg)

private external fun _testSendStringThenUppercaseThenReceiveStringImpl(arg: String): String