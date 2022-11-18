package com.kaeonx.nymandroidport

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.kaeonx.nymandroidport.databinding.ActivityMainBinding

private const val TAG = "mainActivity"

class MainActivity : AppCompatActivity() {

    init {
        System.loadLibrary("sphinx_jni")
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use this to identify the device's ABI.
        Log.i(TAG, "This device's ABI is ${android.os.Build.SUPPORTED_ABIS[0]}.")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val key = ByteArray(STREAM_CIPHER_KEY_SIZE) { i -> (i + 1).toByte() }
        val iv = ByteArray(STREAM_CIPHER_KEY_SIZE) { i -> (STREAM_CIPHER_KEY_SIZE - i).toByte() }
        Log.i(TAG, "key is [${key.joinToString()}]")
        Log.i(TAG, "iv is [${iv.joinToString()}]")
        val generatedBytes = generatePseudorandomBytes(key, iv, 10).map { byte -> byte.toUByte() }
        Log.i(TAG, "generatedBytes are [${generatedBytes.joinToString()}]")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}