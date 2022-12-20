package com.kaeonx.nymandroidport

import androidx.test.ext.junit.runners.AndroidJUnit4
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
class SendingNullableToRustInstrumentedTest {

    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit()
    }

    @Test
    fun sendNullableBooleanTrue() {
        val result = _testSendNullableBooleanThenReceiveString(true)
        val expected = "Rust received Some(true) (Boolean?/Option<bool>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableBooleanFalse() {
        val result = _testSendNullableBooleanThenReceiveString(false)
        val expected = "Rust received Some(false) (Boolean?/Option<bool>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableBooleanNull() {
        val result = _testSendNullableBooleanThenReceiveString(null)
        val expected = "Rust received None (Boolean?/Option<bool>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableByteMin() {
        val result = _testSendNullableByteThenReceiveString(Byte.MIN_VALUE)
        val expected = "Rust received Some(${Byte.MIN_VALUE}) (Byte?/Option<i8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableByteMax() {
        val result = _testSendNullableByteThenReceiveString(Byte.MAX_VALUE)
        val expected = "Rust received Some(${Byte.MAX_VALUE}) (Byte?/Option<i8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableByteNull() {
        val result = _testSendNullableByteThenReceiveString(null)
        val expected = "Rust received None (Byte?/Option<i8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUByteMin() {
        val result = _testSendNullableUByteThenReceiveString(UByte.MIN_VALUE)
        val expected = "Rust received Some(${UByte.MIN_VALUE}) (UByte?/Option<u8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUByteMax() {
        val result = _testSendNullableUByteThenReceiveString(UByte.MAX_VALUE)
        val expected = "Rust received Some(${UByte.MAX_VALUE}) (UByte?/Option<u8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUByteNull() {
        val result = _testSendNullableUByteThenReceiveString(null)
        val expected = "Rust received None (UByte?/Option<u8>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableShortMin() {
        val result = _testSendNullableShortThenReceiveString(Short.MIN_VALUE)
        val expected = "Rust received Some(${Short.MIN_VALUE}) (Short?/Option<i16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableShortMax() {
        val result = _testSendNullableShortThenReceiveString(Short.MAX_VALUE)
        val expected = "Rust received Some(${Short.MAX_VALUE}) (Short?/Option<i16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableShortNull() {
        val result = _testSendNullableShortThenReceiveString(null)
        val expected = "Rust received None (Short?/Option<i16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUShortMin() {
        val result = _testSendNullableUShortThenReceiveString(UShort.MIN_VALUE)
        val expected = "Rust received Some(${UShort.MIN_VALUE}) (UShort?/Option<u16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUShortMax() {
        val result = _testSendNullableUShortThenReceiveString(UShort.MAX_VALUE)
        val expected = "Rust received Some(${UShort.MAX_VALUE}) (UShort?/Option<u16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUShortNull() {
        val result = _testSendNullableUShortThenReceiveString(null)
        val expected = "Rust received None (UShort?/Option<u16>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableIntMin() {
        val result = _testSendNullableIntThenReceiveString(Int.MIN_VALUE)
        val expected = "Rust received Some(${Int.MIN_VALUE}) (Int?/Option<i32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableIntMax() {
        val result = _testSendNullableIntThenReceiveString(Int.MAX_VALUE)
        val expected = "Rust received Some(${Int.MAX_VALUE}) (Int?/Option<i32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableIntNull() {
        val result = _testSendNullableIntThenReceiveString(null)
        val expected = "Rust received None (Int?/Option<i32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUIntMin() {
        val result = _testSendNullableUIntThenReceiveString(UInt.MIN_VALUE)
        val expected = "Rust received Some(${UInt.MIN_VALUE}) (UInt?/Option<u32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUIntMax() {
        val result = _testSendNullableUIntThenReceiveString(UInt.MAX_VALUE)
        val expected = "Rust received Some(${UInt.MAX_VALUE}) (UInt?/Option<u32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableUIntNull() {
        val result = _testSendNullableUIntThenReceiveString(null)
        val expected = "Rust received None (UInt?/Option<u32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableLongMin() {
        val result = _testSendNullableLongThenReceiveString(Long.MIN_VALUE)
        val expected = "Rust received Some(${Long.MIN_VALUE}) (Long?/Option<i64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableLongMax() {
        val result = _testSendNullableLongThenReceiveString(Long.MAX_VALUE)
        val expected = "Rust received Some(${Long.MAX_VALUE}) (Long?/Option<i64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableLongNull() {
        val result = _testSendNullableLongThenReceiveString(null)
        val expected = "Rust received None (Long?/Option<i64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableULongMin() {
        val result = _testSendNullableULongThenReceiveString(ULong.MIN_VALUE)
        val expected = "Rust received Some(${ULong.MIN_VALUE}) (ULong?/Option<u64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableULongMax() {
        val result = _testSendNullableULongThenReceiveString(ULong.MAX_VALUE)
        val expected = "Rust received Some(${ULong.MAX_VALUE}) (ULong?/Option<u64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableULongNull() {
        val result = _testSendNullableULongThenReceiveString(null)
        val expected = "Rust received None (ULong?/Option<u64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatMin() {
        val result = _testSendNullableFloatThenReceiveString(Float.MIN_VALUE)
        val expected =
            "Rust received 0bSome(${Float.MIN_VALUE.toRawBits().toUInt()}) (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatMax() {
        val result = _testSendNullableFloatThenReceiveString(Float.MAX_VALUE)
        val expected =
            "Rust received 0bSome(${Float.MAX_VALUE.toRawBits().toUInt()}) (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatNegInf() {
        val result = _testSendNullableFloatThenReceiveString(Float.NEGATIVE_INFINITY)
        val expected =
            "Rust received 0bSome(${
                Float.NEGATIVE_INFINITY.toRawBits().toUInt()
            }) (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatPosInf() {
        val result = _testSendNullableFloatThenReceiveString(Float.POSITIVE_INFINITY)
        val expected =
            "Rust received 0bSome(${
                Float.POSITIVE_INFINITY.toRawBits().toUInt()
            }) (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatNan() {
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
        val result = _testSendNullableFloatThenReceiveString(nanValue)
        val expected = "Rust received 0bSome(${nanValue.toRawBits().toUInt()}) (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableFloatNull() {
        val result = _testSendNullableFloatThenReceiveString(null)
        val expected = "Rust received 0bNone (Float?/Option<f32>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoubleMin() {
        val result = _testSendNullableDoubleThenReceiveString(Double.MIN_VALUE)
        val expected =
            "Rust received 0bSome(${Double.MIN_VALUE.toRawBits().toULong()}) (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoubleMax() {
        val result = _testSendNullableDoubleThenReceiveString(Double.MAX_VALUE)
        val expected =
            "Rust received 0bSome(${Double.MAX_VALUE.toRawBits().toULong()}) (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoubleNegInf() {
        val result = _testSendNullableDoubleThenReceiveString(Double.NEGATIVE_INFINITY)
        val expected =
            "Rust received 0bSome(${
                Double.NEGATIVE_INFINITY.toRawBits().toULong()
            }) (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoublePosInf() {
        val result = _testSendNullableDoubleThenReceiveString(Double.POSITIVE_INFINITY)
        val expected =
            "Rust received 0bSome(${
                Double.POSITIVE_INFINITY.toRawBits().toULong()
            }) (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoubleNan() {
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
        val result = _testSendNullableDoubleThenReceiveString(nanValue)
        val expected =
            "Rust received 0bSome(${nanValue.toRawBits().toULong()}) (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableDoubleNull() {
        val result = _testSendNullableDoubleThenReceiveString(null)
        val expected = "Rust received 0bNone (Double?/Option<f64>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableStringThenUppercase() {
        val result =
            _testSendNullableStringThenUppercaseThenReceiveString(PRE_DETERMINED_STRING.lowercase())
        val expected =
            "Rust received Some(\"${PRE_DETERMINED_STRING.lowercase()}\"), returning Some(\"${PRE_DETERMINED_STRING.uppercase()}\") (String?/Option<String>)"
        assertEquals(expected, result)
    }

    @Test
    fun sendNullableStringThenUppercaseNull() {
        val result = _testSendNullableStringThenUppercaseThenReceiveString(null)
        val expected = "Rust received None, returning None (String?/Option<String>)"
        assertEquals(expected, result)
    }

}
// Eg. can load library
// Eg. can pass variables correct
// AWS Device Farm for evaluation ** 1000 free minutes on sign up