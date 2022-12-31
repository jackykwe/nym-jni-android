package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_CLASS_NAME
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_PACKAGE_NAME
import com.kaeonx.nymandroidport.jni.getAddress
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.services.NymRunService
import com.kaeonx.nymandroidport.workers.NYMRUNWORKER_CLIENT_ID_KEY
import com.kaeonx.nymandroidport.workers.NymRunWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "clientInfoViewModel"
private const val NYM_RUN_UNIQUE_WORK_NAME = "nymRunUWN"
internal const val SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY = "selectedClientNameSPK"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    // Shared Preferences to store selected client name
    private fun getSharedPref() = getAppContext().getSharedPreferences(
        getAppContext().getString(
            com.kaeonx.nymandroidport.R.string.preference_file_key
        ),
        Context.MODE_PRIVATE
    )

    private val _clientInfoScreenUIState =
        MutableStateFlow(ClientInfoScreenUIState(listOf(), null, null))
    internal val clientInfoScreenUIState = _clientInfoScreenUIState.asStateFlow()

    private suspend fun updateClientInfoScreenUIState() {
        lateinit var clients: List<String>
        var selectedClientName: String? = null
        var selectedClientAddress: String? = null

        withContext(Dispatchers.IO) {
            val clientsDir = getClientsDir()
            // null if clientsDir doesn't exist (app run for very first time after install)
            clients = clientsDir.list()?.asList() ?: listOf()
        }

        getSharedPref().getString(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY, null)?.let {
            selectedClientName = it
            withContext(Dispatchers.Default) {
                selectedClientAddress = getAddress(it)
            }
        }

        _clientInfoScreenUIState.value =
            ClientInfoScreenUIState(clients, selectedClientName, selectedClientAddress)
    }

    // Only run once, when ClientInfoScreen is launched and so the ClientInfoViewModel is created
    // (lasts across activity recreation)
    init {
        // coroutines are launched on UI thread (Dispatches.Main) by default
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // sets up logging on Rust side
                topLevelInit(getAppContext().filesDir.absolutePath)
            }
            updateClientInfoScreenUIState()
        }
    }

    // Under the hood, WorkManager manages and runs a foreground service on your behalf to execute
    // the WorkRequest.
    private val nymRunWorkManager = WorkManager.getInstance(application)
    internal val nymRunWorkInfo = nymRunWorkManager.getWorkInfosForUniqueWorkLiveData(
        NYM_RUN_UNIQUE_WORK_NAME
    )

    private fun runClient(clientName: String) {
        val nymRunServiceName = NymRunService::class.java.name
        val componentName = ComponentName(getAppContext().packageName, nymRunServiceName)

        // TODO: empty constraints; enable during evaluation
        val constraints = Constraints.Builder().build()
        val request = OneTimeWorkRequestBuilder<NymRunWorker>(
//                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
//                TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
//                .setBackoffCriteria(
//                    BackoffPolicy.LINEAR,
//                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
//                    TimeUnit.MILLISECONDS
//                )
            .setInputData(
                workDataOf(
                    ARGUMENT_PACKAGE_NAME to componentName.packageName,  // necessary for RemoteCoroutineWorker to know which process to bind to
                    ARGUMENT_CLASS_NAME to componentName.className,  // necessary for RemoteCoroutineWorker to know which process to bind to
                    NYMRUNWORKER_CLIENT_ID_KEY to clientName
                )
            )
            .build()
        nymRunWorkManager.enqueueUniqueWork(
            NYM_RUN_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,  // automatically cancels the existing work
            request
        )
    }

    internal fun selectClient(clientName: String) {
        runClient(clientName)

        viewModelScope.launch {
            with(getSharedPref().edit()) {
                putString(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY, clientName)
                commit()
            }
            updateClientInfoScreenUIState()
        }
    }

    private var cancelJob: Job? = null  //
    private fun stopRunningClient() {
        val nymRunServicePid =
            getAppContext().getSystemService<ActivityManager>()?.runningAppProcesses?.getOrNull(1)?.pid
        if (nymRunServicePid != null) {
            Log.w(TAG, "SIGINT-ing PID: $nymRunServicePid")
            android.os.Process.sendSignal(nymRunServicePid, 2)  // SIGINT
        }
        cancelJob = viewModelScope.launch {
            cancelJob?.cancelAndJoin()
            do {
                Log.d(TAG, "Waiting for 5s, work potentially still running...")
                delay(5000L)
            } while (nymRunWorkInfo.value!!.any { workInfo -> workInfo.state == WorkInfo.State.RUNNING })
            Log.w(TAG, "Cancelling unique work")
            nymRunWorkManager.cancelUniqueWork(NYM_RUN_UNIQUE_WORK_NAME)
        }
    }

    internal fun unselectClient() {
        stopRunningClient()

        viewModelScope.launch {
            with(getSharedPref().edit()) {
                remove(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY)
                commit()
            }
            updateClientInfoScreenUIState()
        }
    }

    internal fun addClient(newClientName: String, callback: (Boolean) -> Unit) {
        val nonEmptyNewClientName = newClientName.ifEmpty { "client" }

        if (_clientInfoScreenUIState.value.clients.contains(nonEmptyNewClientName)) {
            // Already exists, just run
            selectClient(nonEmptyNewClientName)
            callback(true)
        } else {
            // Create new
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        nymInit(nonEmptyNewClientName)
                    }
                    selectClient(nonEmptyNewClientName)
                    callback(true)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    callback(false)
                }
            }
        }
    }

    internal fun deleteClient(clientName: String, callback: () -> Unit) {
        stopRunningClient()
        viewModelScope.launch {
            val clientsDir = getClientsDir()
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientName).deleteRecursively()
            }

            with(getSharedPref().edit()) {
                remove(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY)
                commit()
            }
            updateClientInfoScreenUIState()

            callback()
        }
    }

}