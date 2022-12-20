package com.kaeonx.nymandroidport

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaeonx.nymandroidport.jni.instrumentedtesthelpers._testReceivePreDeterminedString
import com.kaeonx.nymandroidport.jni.topLevelInit
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val PRE_DETERMINED_STRING = "the brown fox jumps over the lazy dog"

/**
 * The instrumented tests are meant to test that the data flows from Kotlin and vice-versa
 * properly. Therefore, we need to be able to observe what each side (Kotlin/Rust) sees, but since
 * the instrumented tests are run from Kotlin, we are only able to directly observe what Kotlin
 * sees. We need a way to report what Rust sees over to Kotlin. I'm using Strings for this purpose,
 * and therefore, the tests in "Sending...InstrumentedTest.kt" all rely on Strings passing over
 * properly from Rust.
 *
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PreRequisiteInstrumentedTest {

    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit()  // WHAT WENT WRONG??? FIGURE WHEN AWAKE
    }

    @Test
    fun receivePreDeterminedString() {
        val result = _testReceivePreDeterminedString()
        val expected = PRE_DETERMINED_STRING
        assertEquals(expected, result)
    }

}