@file:Suppress("FunctionName")

package com.kaeonx.nymandroidport.jni.instrumentedtesthelpers

internal fun _testReceiveBooleanTrue(): Boolean = _testReceiveBooleanTrueImpl()
private external fun _testReceiveBooleanTrueImpl(): Boolean

internal fun _testReceiveBooleanFalse(): Boolean = _testReceiveBooleanFalseImpl()
private external fun _testReceiveBooleanFalseImpl(): Boolean

internal fun _testReceiveByteMin(): Byte = _testReceiveByteMinImpl()
private external fun _testReceiveByteMinImpl(): Byte

internal fun _testReceiveByteMax(): Byte = _testReceiveByteMaxImpl()
private external fun _testReceiveByteMaxImpl(): Byte

internal fun _testReceiveUByteMin(): UByte = _testReceiveUByteMinImpl()
private external fun _testReceiveUByteMinImpl(): UByte

internal fun _testReceiveUByteMax(): UByte = _testReceiveUByteMaxImpl()
private external fun _testReceiveUByteMaxImpl(): UByte

internal fun _testReceiveShortMin(): Short = _testReceiveShortMinImpl()
private external fun _testReceiveShortMinImpl(): Short

internal fun _testReceiveShortMax(): Short = _testReceiveShortMaxImpl()
private external fun _testReceiveShortMaxImpl(): Short

internal fun _testReceiveUShortMin(): UShort = _testReceiveUShortMinImpl()
private external fun _testReceiveUShortMinImpl(): UShort

internal fun _testReceiveUShortMax(): UShort = _testReceiveUShortMaxImpl()
private external fun _testReceiveUShortMaxImpl(): UShort

internal fun _testReceiveIntMin(): Int = _testReceiveIntMinImpl()
private external fun _testReceiveIntMinImpl(): Int

internal fun _testReceiveIntMax(): Int = _testReceiveIntMaxImpl()
private external fun _testReceiveIntMaxImpl(): Int

internal fun _testReceiveUIntMin(): UInt = _testReceiveUIntMinImpl()
private external fun _testReceiveUIntMinImpl(): UInt

internal fun _testReceiveUIntMax(): UInt = _testReceiveUIntMaxImpl()
private external fun _testReceiveUIntMaxImpl(): UInt

internal fun _testReceiveLongMin(): Long = _testReceiveLongMinImpl()
private external fun _testReceiveLongMinImpl(): Long

internal fun _testReceiveLongMax(): Long = _testReceiveLongMaxImpl()
private external fun _testReceiveLongMaxImpl(): Long

internal fun _testReceiveULongMin(): ULong = _testReceiveULongMinImpl()
private external fun _testReceiveULongMinImpl(): ULong

internal fun _testReceiveULongMax(): ULong = _testReceiveULongMaxImpl()
private external fun _testReceiveULongMaxImpl(): ULong

internal fun _testReceiveFloatMin(): Float = _testReceiveFloatMinImpl()
private external fun _testReceiveFloatMinImpl(): Float

internal fun _testReceiveFloatMax(): Float = _testReceiveFloatMaxImpl()
private external fun _testReceiveFloatMaxImpl(): Float

internal fun _testReceiveFloatNegInf(): Float = _testReceiveFloatNegInfImpl()
private external fun _testReceiveFloatNegInfImpl(): Float

internal fun _testReceiveFloatPosInf(): Float = _testReceiveFloatPosInfImpl()
private external fun _testReceiveFloatPosInfImpl(): Float

internal fun _testReceiveFloatNan(): Float = _testReceiveFloatNanImpl()
private external fun _testReceiveFloatNanImpl(): Float

internal fun _testReceiveDoubleMin(): Double = _testReceiveDoubleMinImpl()
private external fun _testReceiveDoubleMinImpl(): Double

internal fun _testReceiveDoubleMax(): Double = _testReceiveDoubleMaxImpl()
private external fun _testReceiveDoubleMaxImpl(): Double

internal fun _testReceiveDoubleNegInf(): Double = _testReceiveDoubleNegInfImpl()
private external fun _testReceiveDoubleNegInfImpl(): Double

internal fun _testReceiveDoublePosInf(): Double = _testReceiveDoublePosInfImpl()
private external fun _testReceiveDoublePosInfImpl(): Double

internal fun _testReceiveDoubleNan(): Double = _testReceiveDoubleNanImpl()
private external fun _testReceiveDoubleNanImpl(): Double

// Already in PreRequisiteHelpers.kt
// internal fun _testReceivePreDeterminedString(): String = _testReceivePreDeterminedStringImpl()
// private external fun _testReceivePreDeterminedStringImpl(): String