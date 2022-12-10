package com.kaeonx.nymandroidport

internal fun testSendAndReceiveNonNullableBoolean(arg: Boolean) =
    testSendAndReceiveNonNullableBooleanImpl(arg)

private external fun testSendAndReceiveNonNullableBooleanImpl(arg: Boolean)

internal fun testSendAndReceiveNonNullableByte(arg: Byte) =
    testSendAndReceiveNonNullableByteImpl(arg)

private external fun testSendAndReceiveNonNullableByteImpl(arg: Byte)

internal fun testSendAndReceiveNonNullableChar(arg: Char) =
    testSendAndReceiveNonNullableCharImpl(arg)

private external fun testSendAndReceiveNonNullableCharImpl(arg: Char)

internal fun testSendAndReceiveNonNullableShort(arg: Short) =
    testSendAndReceiveNonNullableShortImpl(arg)

private external fun testSendAndReceiveNonNullableShortImpl(arg: Short)

internal fun testSendAndReceiveNonNullableInt(arg: Int) = testSendAndReceiveNonNullableIntImpl(arg)
private external fun testSendAndReceiveNonNullableIntImpl(arg: Int)

internal fun testSendAndReceiveNonNullableLong(arg: Long) =
    testSendAndReceiveNonNullableLongImpl(arg)

private external fun testSendAndReceiveNonNullableLongImpl(arg: Long)

internal fun testSendAndReceiveNonNullableFloat(arg: Float) =
    testSendAndReceiveNonNullableFloatImpl(arg)

private external fun testSendAndReceiveNonNullableFloatImpl(arg: Float)

internal fun testSendAndReceiveNonNullableDouble(arg: Double) =
    testSendAndReceiveNonNullableDoubleImpl(arg)

private external fun testSendAndReceiveNonNullableDoubleImpl(arg: Double)

internal fun testSendAndReceiveNonNullableVoid(arg: Void) =
    testSendAndReceiveNonNullableVoidImpl(arg)

private external fun testSendAndReceiveNonNullableVoidImpl(arg: Void)

internal fun testSendAndReceiveNullableBoolean(arg: Boolean?) =
    testSendAndReceiveNullableBooleanImpl(arg)

private external fun testSendAndReceiveNullableBooleanImpl(arg: Boolean?)

internal fun testSendAndReceiveNullableByte(arg: Byte?) = testSendAndReceiveNullableByteImpl(arg)
private external fun testSendAndReceiveNullableByteImpl(arg: Byte?)

internal fun testSendAndReceiveNullableChar(arg: Char?) = testSendAndReceiveNullableCharImpl(arg)
private external fun testSendAndReceiveNullableCharImpl(arg: Char?)

internal fun testSendAndReceiveNullableShort(arg: Short?) = testSendAndReceiveNullableShortImpl(arg)
private external fun testSendAndReceiveNullableShortImpl(arg: Short?)

internal fun testSendAndReceiveNullableInt(arg: Int?) = testSendAndReceiveNullableIntImpl(arg)
private external fun testSendAndReceiveNullableIntImpl(arg: Int?)

internal fun testSendAndReceiveNullableLong(arg: Long?) = testSendAndReceiveNullableLongImpl(arg)
private external fun testSendAndReceiveNullableLongImpl(arg: Long?)

internal fun testSendAndReceiveNullableFloat(arg: Float?) = testSendAndReceiveNullableFloatImpl(arg)
private external fun testSendAndReceiveNullableFloatImpl(arg: Float?)

internal fun testSendAndReceiveNullableDouble(arg: Double?) =
    testSendAndReceiveNullableDoubleImpl(arg)

private external fun testSendAndReceiveNullableDoubleImpl(arg: Double?)

internal fun testSendAndReceiveNullableVoid(arg: Void?) = testSendAndReceiveNullableVoidImpl(arg)
private external fun testSendAndReceiveNullableVoidImpl(arg: Void?)