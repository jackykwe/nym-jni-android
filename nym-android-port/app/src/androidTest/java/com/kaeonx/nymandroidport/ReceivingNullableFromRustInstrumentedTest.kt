package com.kaeonx.nymandroidport

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kaeonx.nymandroidport.jni.instrumentedtesthelpers.*
import com.kaeonx.nymandroidport.jni.topLevelInit
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val PRE_DETERMINED_STRING = "the brown fox jumps over the lazy dog"

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ReceivingNullableFromRustInstrumentedTest {

    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit(InstrumentationRegistry.getInstrumentation().targetContext.filesDir.absolutePath)
    }

    @Test
    fun receiveNullableBooleanTrue() {
        val result = _testReceiveNullableBooleanTrue()
        @Suppress("RedundantNullableReturnType") val expected: Boolean? = true
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableBooleanFalse() {
        val result = _testReceiveNullableBooleanFalse()
        @Suppress("RedundantNullableReturnType") val expected: Boolean? = false
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableBooleanNull() {
        val result = _testReceiveNullableBooleanNull()
        val expected: Boolean? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableByteMin() {
        val result = _testReceiveNullableByteMin()
        @Suppress("RedundantNullableReturnType") val expected: Byte? = Byte.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableByteMax() {
        val result = _testReceiveNullableByteMax()
        @Suppress("RedundantNullableReturnType") val expected: Byte? = Byte.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableByteNull() {
        val result = _testReceiveNullableByteNull()
        val expected: Byte? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUByteMin() {
        val result = _testReceiveNullableUByteMin()
        @Suppress("RedundantNullableReturnType") val expected: UByte? = UByte.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUByteMax() {
        val result = _testReceiveNullableUByteMax()
        @Suppress("RedundantNullableReturnType") val expected: UByte? = UByte.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUByteNull() {
        val result = _testReceiveNullableUByteNull()
        val expected: UByte? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableShortMin() {
        val result = _testReceiveNullableShortMin()
        @Suppress("RedundantNullableReturnType") val expected: Short? = Short.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableShortMax() {
        val result = _testReceiveNullableShortMax()
        @Suppress("RedundantNullableReturnType") val expected: Short? = Short.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableShortNull() {
        val result = _testReceiveNullableShortNull()
        val expected: Short? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUShortMin() {
        val result = _testReceiveNullableUShortMin()
        @Suppress("RedundantNullableReturnType") val expected: UShort? = UShort.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUShortMax() {
        val result = _testReceiveNullableUShortMax()
        @Suppress("RedundantNullableReturnType") val expected: UShort? = UShort.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUShortNull() {
        val result = _testReceiveNullableUShortNull()
        val expected: UShort? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableIntMin() {
        val result = _testReceiveNullableIntMin()
        @Suppress("RedundantNullableReturnType") val expected: Int? = Int.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableIntMax() {
        val result = _testReceiveNullableIntMax()
        @Suppress("RedundantNullableReturnType") val expected: Int? = Int.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableIntNull() {
        val result = _testReceiveNullableIntNull()
        val expected: Int? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUIntMin() {
        val result = _testReceiveNullableUIntMin()
        @Suppress("RedundantNullableReturnType") val expected: UInt? = UInt.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUIntMax() {
        val result = _testReceiveNullableUIntMax()
        @Suppress("RedundantNullableReturnType") val expected: UInt? = UInt.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableUIntNull() {
        val result = _testReceiveNullableUIntNull()
        val expected: UInt? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableLongMin() {
        val result = _testReceiveNullableLongMin()
        @Suppress("RedundantNullableReturnType") val expected: Long? = Long.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableLongMax() {
        val result = _testReceiveNullableLongMax()
        @Suppress("RedundantNullableReturnType") val expected: Long? = Long.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableLongNull() {
        val result = _testReceiveNullableLongNull()
        val expected: Long? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableULongMin() {
        val result = _testReceiveNullableULongMin()
        @Suppress("RedundantNullableReturnType") val expected: ULong? = ULong.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableULongMax() {
        val result = _testReceiveNullableULongMax()
        @Suppress("RedundantNullableReturnType") val expected: ULong? = ULong.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableULongNull() {
        val result = _testReceiveNullableULongNull()
        val expected: ULong? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatMin() {
        val result = _testReceiveNullableFloatMin()
        @Suppress("RedundantNullableReturnType") val expected: Float? = Float.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatMax() {
        val result = _testReceiveNullableFloatMax()
        @Suppress("RedundantNullableReturnType") val expected: Float? = Float.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatNegInf() {
        val result = _testReceiveNullableFloatNegInf()
        @Suppress("RedundantNullableReturnType") val expected: Float? = Float.NEGATIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatPosInf() {
        val result = _testReceiveNullableFloatPosInf()
        @Suppress("RedundantNullableReturnType") val expected: Float? = Float.POSITIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatNan() {
        val result = _testReceiveNullableFloatNan()
        @Suppress("RedundantNullableReturnType") val expected: Float? = Float.NaN
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableFloatNull() {
        val result = _testReceiveNullableFloatNull()
        val expected: Float? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoubleMin() {
        val result = _testReceiveNullableDoubleMin()
        @Suppress("RedundantNullableReturnType") val expected: Double? = Double.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoubleMax() {
        val result = _testReceiveNullableDoubleMax()
        @Suppress("RedundantNullableReturnType") val expected: Double? = Double.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoubleNegInf() {
        val result = _testReceiveNullableDoubleNegInf()
        @Suppress("RedundantNullableReturnType") val expected: Double? = Double.NEGATIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoublePosInf() {
        val result = _testReceiveNullableDoublePosInf()
        @Suppress("RedundantNullableReturnType") val expected: Double? = Double.POSITIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoubleNan() {
        val result = _testReceiveNullableDoubleNan()
        @Suppress("RedundantNullableReturnType") val expected: Double? = Double.NaN
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullableDoubleNull() {
        val result = _testReceiveNullableDoubleNull()
        val expected: Double? = null
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullablePreDeterminedString() {
        val result = _testReceiveNullablePreDeterminedString()
        @Suppress("RedundantNullableReturnType") val expected: String? = PRE_DETERMINED_STRING
        assertEquals(expected, result)
    }

    @Test
    fun receiveNullablePreDeterminedStringNull() {
        val result = _testReceiveNullablePreDeterminedStringNull()
        val expected: String? = null
        assertEquals(expected, result)
    }

}