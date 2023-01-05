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
class SendingNonNullableToRustInstrumentedTest {

    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit(InstrumentationRegistry.getInstrumentation().targetContext.filesDir.absolutePath)
    }

    @Test
    fun sendBooleanTrue() {
        val result = _testSendBooleanThenReceiveString(true)
        val expected = "Rust received true (Boolean/bool)"
        assertEquals(expected, result)
    }

    @Test
    fun sendBooleanFalse() {
        val result = _testSendBooleanThenReceiveString(false)
        val expected = "Rust received false (Boolean/bool)"
        assertEquals(expected, result)
    }

    @Test
    fun sendByteMin() {
        val result = _testSendByteThenReceiveString(Byte.MIN_VALUE)
        val expected = "Rust received ${Byte.MIN_VALUE} (Byte/i8)"
        assertEquals(expected, result)
    }

    @Test
    fun sendByteMax() {
        val result = _testSendByteThenReceiveString(Byte.MAX_VALUE)
        val expected = "Rust received ${Byte.MAX_VALUE} (Byte/i8)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUByteMin() {
        val result = _testSendUByteThenReceiveString(UByte.MIN_VALUE)
        val expected = "Rust received ${UByte.MIN_VALUE} (UByte/u8)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUByteMax() {
        val result = _testSendUByteThenReceiveString(UByte.MAX_VALUE)
        val expected = "Rust received ${UByte.MAX_VALUE} (UByte/u8)"
        assertEquals(expected, result)
    }

    @Test
    fun sendShortMin() {
        val result = _testSendShortThenReceiveString(Short.MIN_VALUE)
        val expected = "Rust received ${Short.MIN_VALUE} (Short/i16)"
        assertEquals(expected, result)
    }

    @Test
    fun sendShortMax() {
        val result = _testSendShortThenReceiveString(Short.MAX_VALUE)
        val expected = "Rust received ${Short.MAX_VALUE} (Short/i16)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUShortMin() {
        val result = _testSendUShortThenReceiveString(UShort.MIN_VALUE)
        val expected = "Rust received ${UShort.MIN_VALUE} (UShort/u16)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUShortMax() {
        val result = _testSendUShortThenReceiveString(UShort.MAX_VALUE)
        val expected = "Rust received ${UShort.MAX_VALUE} (UShort/u16)"
        assertEquals(expected, result)
    }

    @Test
    fun sendIntMin() {
        val result = _testSendIntThenReceiveString(Int.MIN_VALUE)
        val expected = "Rust received ${Int.MIN_VALUE} (Int/i32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendIntMax() {
        val result = _testSendIntThenReceiveString(Int.MAX_VALUE)
        val expected = "Rust received ${Int.MAX_VALUE} (Int/i32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUIntMin() {
        val result = _testSendUIntThenReceiveString(UInt.MIN_VALUE)
        val expected = "Rust received ${UInt.MIN_VALUE} (UInt/u32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendUIntMax() {
        val result = _testSendUIntThenReceiveString(UInt.MAX_VALUE)
        val expected = "Rust received ${UInt.MAX_VALUE} (UInt/u32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendLongMin() {
        val result = _testSendLongThenReceiveString(Long.MIN_VALUE)
        val expected = "Rust received ${Long.MIN_VALUE} (Long/i64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendLongMax() {
        val result = _testSendLongThenReceiveString(Long.MAX_VALUE)
        val expected = "Rust received ${Long.MAX_VALUE} (Long/i64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendULongMin() {
        val result = _testSendULongThenReceiveString(ULong.MIN_VALUE)
        val expected = "Rust received ${ULong.MIN_VALUE} (ULong/u64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendULongMax() {
        val result = _testSendULongThenReceiveString(ULong.MAX_VALUE)
        val expected = "Rust received ${ULong.MAX_VALUE} (ULong/u64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendFloatMin() {
        val result = _testSendFloatThenReceiveString(Float.MIN_VALUE)
        val expected = "Rust received 0b${Float.MIN_VALUE.toRawBits().toUInt()} (Float/f32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendFloatMax() {
        val result = _testSendFloatThenReceiveString(Float.MAX_VALUE)
        val expected = "Rust received 0b${Float.MAX_VALUE.toRawBits().toUInt()} (Float/f32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendFloatNegInf() {
        val result = _testSendFloatThenReceiveString(Float.NEGATIVE_INFINITY)
        val expected = "Rust received 0b${Float.NEGATIVE_INFINITY.toRawBits().toUInt()} (Float/f32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendFloatPosInf() {
        val result = _testSendFloatThenReceiveString(Float.POSITIVE_INFINITY)
        val expected = "Rust received 0b${Float.POSITIVE_INFINITY.toRawBits().toUInt()} (Float/f32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendFloatNan() {
        /*
         * A small caveat about floating point representations and qNaNs/sNaNs:
         * Rust's to_bits() function prefers to preserve the exact bit values, without any
         * manipulation. This is what's used on the Rust side of this test suite.
         * Kotlin presents two methods: toBits() and toRawBits(). toRawBits() preserves exact bit
         * values, whereas toBits() returns an Integer that may not reflect the actual bit
         * representation of the float. Through manual testing, I discovered that 0f / 0f fleshes
         * out this difference.
         *
         * Tl;dr: use toRawBits().
         *
         *     println("${(1.0f / 0f).toBits().toUInt()}")                                             // 2139095040
         *     println("${kotlin.math.sqrt(-1f).toBits().toUInt()}")                                   // 2143289344
         *     println("${(0f / 0f).toBits().toUInt()}")                                               // 2143289344
         *     println("${(Float.POSITIVE_INFINITY / Float.POSITIVE_INFINITY).toBits().toUInt()}")     // 2143289344
         *     println("${(Float.NaN).toBits().toUInt()}")                                             // 2143289344
         *     println()
         *     println("${(1.0f / 0f).toRawBits().toUInt()}")                                          // 2139095040
         *     println("${kotlin.math.sqrt(-1f).toRawBits().toUInt()}")                                // 4290772992
         *     println("${(0f / 0f).toRawBits().toUInt()}")                                            // 4290772992
         *     println("${(Float.POSITIVE_INFINITY / Float.POSITIVE_INFINITY).toRawBits().toUInt()}")  // 4290772992
         *     println("${(Float.NaN).toRawBits().toUInt()}")                                          // 2143289344
         */
        val nanValue = 0f / 0f
        val result = _testSendFloatThenReceiveString(nanValue)
        val expected = "Rust received 0b${nanValue.toRawBits().toUInt()} (Float/f32)"
        assertEquals(expected, result)
    }

    @Test
    fun sendDoubleMin() {
        val result = _testSendDoubleThenReceiveString(Double.MIN_VALUE)
        val expected = "Rust received 0b${Double.MIN_VALUE.toRawBits().toULong()} (Double/f64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendDoubleMax() {
        val result = _testSendDoubleThenReceiveString(Double.MAX_VALUE)
        val expected = "Rust received 0b${Double.MAX_VALUE.toRawBits().toULong()} (Double/f64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendDoubleNegInf() {
        val result = _testSendDoubleThenReceiveString(Double.NEGATIVE_INFINITY)
        val expected =
            "Rust received 0b${Double.NEGATIVE_INFINITY.toRawBits().toULong()} (Double/f64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendDoublePosInf() {
        val result = _testSendDoubleThenReceiveString(Double.POSITIVE_INFINITY)
        val expected =
            "Rust received 0b${Double.POSITIVE_INFINITY.toRawBits().toULong()} (Double/f64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendDoubleNan() {
        /*
         * A small caveat about floating point representations and qNaNs/sNaNs:
         * Rust's to_bits() function prefers to preserve the exact bit values, without any
         * manipulation. This is what's used on the Rust side of this test suite.
         * Kotlin presents two methods: toBits() and toRawBits(). toRawBits() preserves exact bit
         * values, whereas toBits() returns a Long that may not reflect the actual bit
         * representation of the double. Through manual testing, I discovered that 0.0 / 0.0 fleshes
         * out this difference.
         *
         * Tl;dr: use toRawBits().
         *
         *     println("${(1.0 / 0.0).toBits().toULong()}")                                               // 9218868437227405312
         *     println("${kotlin.math.sqrt(-1.0).toBits().toULong()}")                                    // 9221120237041090560
         *     println("${(0.0 / 0.0).toBits().toULong()}")                                               // 9221120237041090560
         *     println("${(Double.POSITIVE_INFINITY / Double.POSITIVE_INFINITY).toBits().toULong()}")     // 9221120237041090560
         *     println("${(Double.NaN).toBits().toULong()}")                                              // 9221120237041090560
         *     println()
         *     println("${(1.0 / 0.0).toRawBits().toULong()}")                                            // 9218868437227405312
         *     println("${kotlin.math.sqrt(-1.0).toRawBits().toULong()}")                                 // 18444492273895866368
         *     println("${(0.0 / 0.0).toRawBits().toULong()}")                                            // 18444492273895866368
         *     println("${(Double.POSITIVE_INFINITY / Double.POSITIVE_INFINITY).toRawBits().toULong()}")  // 18444492273895866368
         *     println("${(Double.NaN).toRawBits().toULong()}")                                           // 9221120237041090560
         */
        val nanValue = 0.0 / 0.0
        val result = _testSendDoubleThenReceiveString(nanValue)
        val expected = "Rust received 0b${nanValue.toRawBits().toULong()} (Double/f64)"
        assertEquals(expected, result)
    }

    @Test
    fun sendStringThenUppercase() {
        val result =
            _testSendStringThenUppercaseThenReceiveString(PRE_DETERMINED_STRING.lowercase())
        val expected =
            "Rust received ${PRE_DETERMINED_STRING.lowercase()}, returning ${PRE_DETERMINED_STRING.uppercase()} (String/String)"
        assertEquals(expected, result)
    }

}