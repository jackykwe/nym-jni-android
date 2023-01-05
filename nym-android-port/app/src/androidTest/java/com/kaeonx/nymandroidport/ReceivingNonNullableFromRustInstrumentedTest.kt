package com.kaeonx.nymandroidport

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.kaeonx.nymandroidport.jni.instrumentedtesthelpers.*
import com.kaeonx.nymandroidport.jni.topLevelInit
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ReceivingNonFromRustInstrumentedTest {

    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit(InstrumentationRegistry.getInstrumentation().targetContext.filesDir.absolutePath)
    }

    @Test
    fun receiveBooleanTrue() {
        val result = _testReceiveBooleanTrue()
        val expected = true
        assertEquals(expected, result)
    }

    @Test
    fun receiveBooleanFalse() {
        val result = _testReceiveBooleanFalse()
        val expected = false
        assertEquals(expected, result)
    }

    @Test
    fun receiveByteMin() {
        val result = _testReceiveByteMin()
        val expected = Byte.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveByteMax() {
        val result = _testReceiveByteMax()
        val expected = Byte.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUByteMin() {
        val result = _testReceiveUByteMin()
        val expected = UByte.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUByteMax() {
        val result = _testReceiveUByteMax()
        val expected = UByte.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveShortMin() {
        val result = _testReceiveShortMin()
        val expected = Short.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveShortMax() {
        val result = _testReceiveShortMax()
        val expected = Short.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUShortMin() {
        val result = _testReceiveUShortMin()
        val expected = UShort.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUShortMax() {
        val result = _testReceiveUShortMax()
        val expected = UShort.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveIntMin() {
        val result = _testReceiveIntMin()
        val expected = Int.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveIntMax() {
        val result = _testReceiveIntMax()
        val expected = Int.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUIntMin() {
        val result = _testReceiveUIntMin()
        val expected = UInt.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveUIntMax() {
        val result = _testReceiveUIntMax()
        val expected = UInt.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveLongMin() {
        val result = _testReceiveLongMin()
        val expected = Long.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveLongMax() {
        val result = _testReceiveLongMax()
        val expected = Long.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveULongMin() {
        val result = _testReceiveULongMin()
        val expected = ULong.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveULongMax() {
        val result = _testReceiveULongMax()
        val expected = ULong.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveFloatMin() {
        val result = _testReceiveFloatMin()
        val expected = Float.MIN_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveFloatMax() {
        val result = _testReceiveFloatMax()
        val expected = Float.MAX_VALUE
        assertEquals(expected, result)
    }

    @Test
    fun receiveFloatNegInf() {
        val result = _testReceiveFloatNegInf()
        val expected = Float.NEGATIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveFloatPosInf() {
        val result = _testReceiveFloatPosInf()
        val expected = Float.POSITIVE_INFINITY
        assertEquals(expected, result)
    }

    @Test
    fun receiveFloatNan() {
        val result = _testReceiveFloatNan()
        val expected = Float.NaN
        assertEquals(expected, result)
    }

    @Test
    fun receiveDoubleMin() {
        val result = _testReceiveDoubleMin()
        val expected = Double.MIN_VALUE
        assertEquals(expected, result, 1e-9)
    }

    @Test
    fun receiveDoubleMax() {
        val result = _testReceiveDoubleMax()
        val expected = Double.MAX_VALUE
        assertEquals(expected, result, 1e-9)
    }

    @Test
    fun receiveDoubleNegInf() {
        val result = _testReceiveDoubleNegInf()
        val expected = Double.NEGATIVE_INFINITY
        assertEquals(expected, result, 1e-9)
    }

    @Test
    fun receiveDoublePosInf() {
        val result = _testReceiveDoublePosInf()
        val expected = Double.POSITIVE_INFINITY
        assertEquals(expected, result, 1e-9)
    }

    @Test
    fun receiveDoubleNan() {
        val result = _testReceiveDoubleNan()
        val expected = Double.NaN
        assertEquals(expected, result, 1e-9)
    }

    // Already in PreRequisiteInstrumentedTest.kt
//    @Test
//    fun receivePreDeterminedString() {
//        val result = _testReceivePreDeterminedString()
//        val expected = PRE_DETERMINED_STRING
//        assertEquals(expected, result)
//    }

}