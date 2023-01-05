package com.kaeonx.nymandroidport.services

import android.util.Log
import androidx.work.multiprocess.RemoteWorkerService
import kotlin.system.exitProcess

private const val TAG = "nymRunService"

// This subclass is defined in case we want to override things in RemoteWorkerService
class NymRunService : RemoteWorkerService() {
    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Exiting process 2")
        exitProcess(0)
    }
}