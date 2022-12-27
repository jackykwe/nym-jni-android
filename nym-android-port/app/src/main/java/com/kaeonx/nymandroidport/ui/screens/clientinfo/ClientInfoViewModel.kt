package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.Application
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.workers.NYMRUNWORKER_CLIENT_ID_KEY
import com.kaeonx.nymandroidport.workers.NymRunWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

private const val TAG = "clientInfoViewModel"
private const val NYM_RUN_UNIQUE_WORK_NAME = "nymRunUWN"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel. Instead, it is guarded behind a
    // function call.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getStorageAbsPath() = getAppContext().filesDir.absolutePath
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    private val _selectedClient: MutableStateFlow<String?> = MutableStateFlow(null)
    val selectedClient: StateFlow<String?> = _selectedClient.asStateFlow()
    private val _clients = listOf<String>().toMutableStateList()
    val clients: List<String>
        get() = _clients.sorted()

    // Under the hood, WorkManager manages and runs a foreground service on your behalf to execute
    // the WorkRequest.
    private val nymRunWorkManager = WorkManager.getInstance(application)
    internal val nymRunWorkInfo = nymRunWorkManager.getWorkInfosForUniqueWorkLiveData(
        NYM_RUN_UNIQUE_WORK_NAME
    )

    private suspend fun refreshClientsList() {
        withContext(Dispatchers.IO) {
            val clientsDir = getClientsDir()
            // null if clientsDir doesn't exist (app run for very first time after install)
            val listOfFiles = clientsDir.list()
            _clients.clear()
            listOfFiles?.toCollection(_clients)
        }
    }

    // Only run once, when ClientInfoScreen is launched and so the ClientInfoViewModel is created
    // (lasts across activity recreation)
    init {
        // coroutines are launched on UI thread (Dispatches.Main) by default
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // sets up logging on Rust side
                topLevelInit()
            }
            refreshClientsList()
        }
    }

    // TODO: Need to make it stoppable
    private fun runClient(clientName: String) {
        // TODO: empty constraints; enable during evaluation
        val constraints = Constraints.Builder().build()

        nymRunWorkManager.enqueueUniqueWork(
            NYM_RUN_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,  // automatically cancels the existing work
            OneTimeWorkRequestBuilder<NymRunWorker>(
//                PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
//                TimeUnit.MILLISECONDS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(
                    workDataOf(
                        NYMRUNWORKER_CLIENT_ID_KEY to clientName
                    )
                )
                .build()
        )
    }

    internal fun selectClient(clientName: String) {
        runClient(clientName)
        _selectedClient.value = clientName
    }

    internal fun stopRunningClient() {
        Log.e(TAG, "STOPPING")
        nymRunWorkManager.cancelUniqueWork(NYM_RUN_UNIQUE_WORK_NAME)
        Log.e(TAG, "STOPPED?")
    }

    internal fun unselectClient() {
        stopRunningClient()
        _selectedClient.value = null
    }

    internal fun addClient(newClientName: String, callback: (Boolean) -> Unit) {
        val nonEmptyNewClientName = newClientName.ifEmpty { "client" }

        if (clients.contains(nonEmptyNewClientName)) {
            // Already exists, just run
            selectClient(nonEmptyNewClientName)
            callback(true)
        } else {
            // Create new
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        nymInit(getStorageAbsPath(), nonEmptyNewClientName)
                    }
                    refreshClientsList()
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
        unselectClient()
        viewModelScope.launch {
            val clientsDir = getClientsDir()
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientName).deleteRecursively()
            }
            refreshClientsList()
            callback()
        }
    }


}