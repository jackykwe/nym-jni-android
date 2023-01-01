package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_CLASS_NAME
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_PACKAGE_NAME
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.jni.getAddress
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.services.NymRunService
import com.kaeonx.nymandroidport.workers.NYMRUNWORKER_CLIENT_ID_KEY
import com.kaeonx.nymandroidport.workers.NymRunWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private const val TAG = "clientInfoViewModel"
private const val NYM_RUN_UNIQUE_WORK_NAME = "nymRunUWN"
internal const val SELECTED_CLIENT_ID_KSVP_KEY =
    "selectedClientId"  // KSVP is KeyStringValuePair
internal const val SELECTED_CLIENT_ADDRESS_KSVP_KEY =
    "selectedClientAddress"  // KSVP is KeyStringValuePair

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    private val keyStringValuePairRepository = KeyStringValuePairRepository(
        AppDatabase.getInstance(getAppContext()).keyStringValuePairDao()
    )

    private suspend fun getClientsList(): List<String> {
        return withContext(Dispatchers.IO) {
            getClientsDir().list()  // null if clientsDir doesn't exist (app run for very first time after install)
                ?.asList()
                ?: listOf()
        }
    }

    // Always up-to-date values in the database; hot flow
    internal val clientInfoScreenUIState =
        keyStringValuePairRepository.get(
            listOf(
                SELECTED_CLIENT_ID_KSVP_KEY,
                SELECTED_CLIENT_ADDRESS_KSVP_KEY
            )
        ).map {
            ClientInfoScreenUIState(
                clients = getClientsList(),
                selectedClientId = it[SELECTED_CLIENT_ID_KSVP_KEY],
                selectedClientAddress = it[SELECTED_CLIENT_ADDRESS_KSVP_KEY]
            )
        }.stateIn(  // turn cold flow into hot flow
            viewModelScope,
            SharingStarted.Eagerly,
            ClientInfoScreenUIState(
                clients = listOf(),
                selectedClientId = null,
                selectedClientAddress = null
            )
        )

    // Only run once, when ClientInfoScreen is launched and so the ClientInfoViewModel is created
    // (lasts across activity recreation)
    init {
        // coroutines are launched on UI thread (Dispatches.Main) by default
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                // sets up logging on Rust side
                topLevelInit(getAppContext().filesDir.absolutePath)
            }
        }
    }

    // Under the hood, WorkManager manages and runs a foreground service on your behalf to execute
    // the WorkRequest.
    private val nymRunWorkManager = WorkManager.getInstance(application)
    internal val nymRunWorkInfo = nymRunWorkManager.getWorkInfosForUniqueWorkLiveData(
        NYM_RUN_UNIQUE_WORK_NAME
    )

    private fun runClient(clientId: String) {
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
                    NYMRUNWORKER_CLIENT_ID_KEY to clientId
                )
            )
            .build()
        nymRunWorkManager.enqueueUniqueWork(
            NYM_RUN_UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,  // automatically cancels the existing work
            request
        )
    }

    internal fun selectClient(clientId: String) {
//        runClient(clientId)
        viewModelScope.launch {
            val clientAddress = withContext(Dispatchers.Default) {
                getAddress(clientId)
            }
            withContext(Dispatchers.IO) {
                keyStringValuePairRepository.put(
                    listOf(
                        SELECTED_CLIENT_ID_KSVP_KEY to clientId,
                        SELECTED_CLIENT_ADDRESS_KSVP_KEY to clientAddress
                    )
                )
            }
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
        viewModelScope.launch(Dispatchers.IO) {
            keyStringValuePairRepository.remove(
                listOf(
                    SELECTED_CLIENT_ID_KSVP_KEY,
                    SELECTED_CLIENT_ADDRESS_KSVP_KEY
                )
            )
        }
    }

    internal fun addClient(newClientId: String, callback: (Boolean) -> Unit) {
        val nonEmptyNewClientId = newClientId.ifEmpty { "client" }

        if (clientInfoScreenUIState.value.clients.contains(nonEmptyNewClientId)) {
            // Already exists, just run
            selectClient(nonEmptyNewClientId)
            callback(true)
        } else {
            // Create new
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        nymInit(nonEmptyNewClientId)
                    }
                    selectClient(nonEmptyNewClientId)
                    callback(true)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    callback(false)
                }
            }
        }
    }

    internal fun deleteClient(clientId: String, callback: () -> Unit) {
        stopRunningClient()
        viewModelScope.launch {
            val clientsDir = getClientsDir()
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientId).deleteRecursively()
            }
            viewModelScope.launch(Dispatchers.IO) {
                keyStringValuePairRepository.remove(
                    listOf(
                        SELECTED_CLIENT_ID_KSVP_KEY,
                        SELECTED_CLIENT_ADDRESS_KSVP_KEY
                    )
                )
            }
            callback()
        }
    }

}