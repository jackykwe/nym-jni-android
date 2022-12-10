package com.kaeonx.nymandroidport

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
class JNIInstrumentedTest {
    @Before
    fun loadJNILibrary() {
        System.loadLibrary("nym_jni")
        topLevelInit()
    }

    @Test
    fun hi() {
        assertEquals(1, 1)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.kaeonx.nymandroidport", appContext.packageName)
    }
}
// Eg. can load library
// Eg. can pass variables correct
// AWS Device Farm for evaluation ** 1000 free minutes on sign up