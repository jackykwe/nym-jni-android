@file:Suppress("FunctionName")

package com.kaeonx.nymandroidport.jni.instrumentedtesthelpers

/**
 * Sends a `Boolean?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableBooleanThenReceiveString(arg: Boolean?): String =
    _testSendNullableBooleanThenReceiveStringImpl(arg)

private external fun _testSendNullableBooleanThenReceiveStringImpl(arg: Boolean?): String

/**
 * Sends a `Byte?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableByteThenReceiveString(arg: Byte?): String =
    _testSendNullableByteThenReceiveStringImpl(arg)

private external fun _testSendNullableByteThenReceiveStringImpl(arg: Byte?): String

/**
 * Sends a `UByte?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableUByteThenReceiveString(arg: UByte?): String =
    _testSendNullableUByteThenReceiveStringImpl(arg)

private external fun _testSendNullableUByteThenReceiveStringImpl(arg: UByte?): String

/**
 * Sends a `Short?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableShortThenReceiveString(arg: Short?): String =
    _testSendNullableShortThenReceiveStringImpl(arg)

private external fun _testSendNullableShortThenReceiveStringImpl(arg: Short?): String

/**
 * Sends a `UShort?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableUShortThenReceiveString(arg: UShort?): String =
    _testSendNullableUShortThenReceiveStringImpl(arg)

private external fun _testSendNullableUShortThenReceiveStringImpl(arg: UShort?): String

/**
 * Sends a `Int?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableIntThenReceiveString(arg: Int?): String =
    _testSendNullableIntThenReceiveStringImpl(arg)

private external fun _testSendNullableIntThenReceiveStringImpl(arg: Int?): String

/**
 * Sends a `UInt?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableUIntThenReceiveString(arg: UInt?): String =
    _testSendNullableUIntThenReceiveStringImpl(arg)

private external fun _testSendNullableUIntThenReceiveStringImpl(arg: UInt?): String

/**
 * Sends a `Long?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableLongThenReceiveString(arg: Long?): String =
    _testSendNullableLongThenReceiveStringImpl(arg)

private external fun _testSendNullableLongThenReceiveStringImpl(arg: Long?): String

/**
 * Sends a `ULong?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableULongThenReceiveString(arg: ULong?): String =
    _testSendNullableULongThenReceiveStringImpl(arg)

private external fun _testSendNullableULongThenReceiveStringImpl(arg: ULong?): String

/**
 * Sends a `Float?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableFloatThenReceiveString(arg: Float?): String =
    _testSendNullableFloatThenReceiveStringImpl(arg)

private external fun _testSendNullableFloatThenReceiveStringImpl(arg: Float?): String

/**
 * Sends a `Double?` from Kotlin to Rust, then receive back a `String` reporting what Rust received.
 */
internal fun _testSendNullableDoubleThenReceiveString(arg: Double?): String =
    _testSendNullableDoubleThenReceiveStringImpl(arg)

private external fun _testSendNullableDoubleThenReceiveStringImpl(arg: Double?): String

/**
 * Sends a `String?` from Kotlin to Rust, then receive a `String` reporting what Rust received, after Rust performs the uppercase transform on the provided `String?`.
 */
internal fun _testSendNullableStringThenUppercaseThenReceiveString(arg: String?): String =
    _testSendNullableStringThenUppercaseThenReceiveStringImpl(arg)

private external fun _testSendNullableStringThenUppercaseThenReceiveStringImpl(arg: String?): String
