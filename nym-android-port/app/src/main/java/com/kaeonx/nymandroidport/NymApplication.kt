package com.kaeonx.nymandroidport

import android.app.Application
import android.util.Log
import androidx.work.Configuration

// Boilerplate
class NymApplication : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setDefaultProcessName(applicationContext.packageName)
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
}