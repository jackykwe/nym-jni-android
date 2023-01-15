package com.kaeonx.nymchatprototype.services

import android.util.Log
import androidx.work.multiprocess.RemoteWorkerService
import kotlin.system.exitProcess

private const val TAG = "nymRunService"

// This subclass is defined in case we want to override things in RemoteWorkerService
class NymRunService : RemoteWorkerService() {
    override fun onCreate() {
        Log.i(TAG, "nymRunService created with pid ${android.os.Process.myPid()}")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Exiting process 2")
        exitProcess(0)
    }
}