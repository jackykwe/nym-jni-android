package com.kaeonx.nymandroidport

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kaeonx.nymandroidport.ui.NymAndroidPortTheme

private const val TAG = "mainActivity"

class MainActivity : ComponentActivity() {

    init {
        // Use this to identify the device's ABI.
        Log.i(TAG, "This device's ABI is ${android.os.Build.SUPPORTED_ABIS[0]}.")
        System.loadLibrary("nym_jni")
        Log.d(TAG, "nym_jni has been successfully loaded")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val key = ByteArray(STREAM_CIPHER_KEY_SIZE) { i -> (i + 1).toByte() }
//        val iv = ByteArray(STREAM_CIPHER_KEY_SIZE) { i -> (STREAM_CIPHER_KEY_SIZE - i).toByte() }
//        Log.i(TAG, "key is [${key.joinToString()}]")
//        Log.i(TAG, "iv is [${iv.joinToString()}]")
//        val generatedBytes = generatePseudorandomBytes(key, iv, 10).map { byte -> byte.toUByte() }
//        Log.i(TAG, "generatedBytes are [${generatedBytes.joinToString()}]")

        setContent {
            NymAndroidPortTheme {
                NymAndroidPortApp()
            }
        }
    }
}