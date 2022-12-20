@file:Suppress("FunctionName")

package com.kaeonx.nymandroidport.jni.instrumentedtesthelpers

/*
 * The instrumented tests are meant to test that the data flows from Kotlin and vice-versa
 * properly. Therefore, we need to be able to observe what each side (Kotlin/Rust) sees, but since
 * the instrumented tests are run from Kotlin, we are only able to directly observe what Kotlin
 * sees. We need a way to report what Rust sees over to Kotlin. I'm using Strings for this purpose,
 * and therefore, the tests in "Sending...InstrumentedTest.kt" all rely on Strings passing over
 * properly from Rust.
 */
/**
 * Receives a pre-determined String from Rust.
 */
internal fun _testReceivePreDeterminedString(): String = _testReceivePreDeterminedStringImpl()
private external fun _testReceivePreDeterminedStringImpl(): String