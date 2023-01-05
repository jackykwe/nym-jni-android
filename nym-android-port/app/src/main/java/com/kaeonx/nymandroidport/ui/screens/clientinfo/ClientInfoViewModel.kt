package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.ActivityManager
import android.app.Application
import android.content.ComponentName
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.*
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_CLASS_NAME
import androidx.work.multiprocess.RemoteListenableWorker.ARGUMENT_PACKAGE_NAME
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ID_KSVP_KEY
import com.kaeonx.nymandroidport.jni.getAddress
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.services.NYMRUNWORKER_CLIENT_ID_KEY
import com.kaeonx.nymandroidport.services.NYM_RUN_PORT
import com.kaeonx.nymandroidport.services.NymRunService
import com.kaeonx.nymandroidport.services.NymRunWorker
import com.kaeonx.nymandroidport.utils.NymRunState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//private const val TAG = "clientInfoViewModel"
private const val NYM_RUN_UNIQUE_WORK_NAME = "nymRunUWN"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    ///////////////////////////////
    // BASIC VIEWMODEL FUNCTIONS //
    ///////////////////////////////

    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    /////////////////////////////////////////
    // REPOSITORIES, WORKMANAGER AND FLOWS //
    /////////////////////////////////////////

    private val keyStringValuePairRepository = KeyStringValuePairRepository(
        AppDatabase.getInstance(getAppContext()).keyStringValuePairDao()
    )
    private val ksvpFlow = keyStringValuePairRepository.get(
        listOf(
            RUNNING_CLIENT_ID_KSVP_KEY,
            RUNNING_CLIENT_ADDRESS_KSVP_KEY,
        )
    )
    private val nymRunStateFlow = keyStringValuePairRepository.get(NYM_RUN_STATE_KSVP_KEY).map {
        NymRunState.valueOf(it ?: NymRunState.IDLE.name)
    }

    // Under the hood, WorkManager manages and runs a foreground service on your behalf to execute
    // the WorkRequest.
    private val workManager = WorkManager.getInstance(application)
    private val nymRunWorkInfoFlow = workManager.getWorkInfosForUniqueWorkLiveData(
        NYM_RUN_UNIQUE_WORK_NAME
    ).asFlow().map {
//        if (it.size > 1) throw IllegalStateException(">1 WorkInfos co-existing")  // required for correctness of next line, and all code dependent on it
        it.getOrNull(0)
    }
    internal val nymRunWorkInfoAllDebugFlow = workManager.getWorkInfosForUniqueWorkLiveData(
        NYM_RUN_UNIQUE_WORK_NAME
    ).asFlow().stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    init {
        workManager.cancelAllWork()
    }

    private suspend fun getClientsList(): List<String> {
        return withContext(Dispatchers.IO) {
            getClientsDir().list()  // null if clientsDir doesn't exist (app run for very first time after install)
                ?.asList()
                ?: listOf()
        }
    }

    // Always up-to-date values in the database; hot flow
    internal val clientInfoScreenUIState =
        combine(
            ksvpFlow,
            nymRunWorkInfoFlow,
            nymRunStateFlow
        ) { ksvpMap, nymRunWorkInfo, nymRunState ->
            viewModelScope.launch {
                if (
                    nymRunState == NymRunState.TEARING_DOWN
                    && nymRunWorkInfo?.state?.isFinished == true
                ) {
                    keyStringValuePairRepository.put(
                        listOf(
                            NYM_RUN_STATE_KSVP_KEY to NymRunState.IDLE.name
                        )
                    )
                }
            }

            ClientInfoScreenUIState(
                clients = getClientsList(),
                selectedClientId = ksvpMap[RUNNING_CLIENT_ID_KSVP_KEY],
                selectedClientAddress = ksvpMap[RUNNING_CLIENT_ADDRESS_KSVP_KEY],
                nymRunState = nymRunState,
                nymRunWorkInfo = nymRunWorkInfo
            )
        }.stateIn(  // turn cold flow into hot flow
            viewModelScope,
            SharingStarted.Eagerly,
            ClientInfoScreenUIState(
                clients = listOf(),
                selectedClientId = null,
                selectedClientAddress = null,
                nymRunState = NymRunState.IDLE,
                nymRunWorkInfo = null
            )
        )

    // TODO, if important
//    private suspend fun fixBrokenState() {
//    }

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

    /////////////////////////////////
    // OPERATIONS CALLABLE FROM UI //
    /////////////////////////////////

    internal fun selectClient(clientId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (clientId == null) {
                keyStringValuePairRepository.remove(
                    listOf(
                        RUNNING_CLIENT_ID_KSVP_KEY,
                        RUNNING_CLIENT_ADDRESS_KSVP_KEY
                    )
                )
            } else {
                val clientAddress = withContext(Dispatchers.Default) {
                    getAddress(clientId)
                }
                keyStringValuePairRepository.put(
                    listOf(
                        RUNNING_CLIENT_ID_KSVP_KEY to clientId,
                        RUNNING_CLIENT_ADDRESS_KSVP_KEY to clientAddress,
                    )
                )
            }
        }
    }

    internal fun enqueueNymRunWork() {
        viewModelScope.launch {
            workManager.pruneWork()  // necessary to preserve the invariant that there exists at most 1 WorkInfo
            withContext(Dispatchers.IO) {
                keyStringValuePairRepository.put(
                    listOf(
                        NYM_RUN_STATE_KSVP_KEY to NymRunState.SETTING_UP.name
                    )
                )
            }

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
                        NYMRUNWORKER_CLIENT_ID_KEY to clientInfoScreenUIState.value.selectedClientId!!
                    )
                )
                .build()
            workManager.enqueueUniqueWork(
                NYM_RUN_UNIQUE_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,  // wait for existing work to terminate
                request
            )
        }
    }

    internal fun stopNymRunWork() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                keyStringValuePairRepository.put(
                    listOf(
                        NYM_RUN_STATE_KSVP_KEY to NymRunState.TEARING_DOWN.name
                    )
                )
            }

            val nymRunServicePid =
                getAppContext().getSystemService<ActivityManager>()?.runningAppProcesses?.getOrNull(
                    1
                )?.pid
            if (nymRunServicePid != null) {
//                Log.w(TAG, "[stopNymRunWork()] SIGINT-ing PID: $nymRunServicePid")
                android.os.Process.sendSignal(nymRunServicePid, 2)  // SIGINT
            }
        }
    }

    internal fun addClient(newClientId: String, callback: (errorMsg: String?) -> Unit) {
        val nonEmptyNewClientId = newClientId.ifEmpty { "client" }

        if (clientInfoScreenUIState.value.clients.contains(nonEmptyNewClientId)) {
            // Already exists
            callback("Client \"$nonEmptyNewClientId\" already exists.")
        } else {
            // Create new
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        nymInit(nonEmptyNewClientId, port = NYM_RUN_PORT)
                    }
                    selectClient(nonEmptyNewClientId)
                    callback(null)  // no error
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    keyStringValuePairRepository.put(
                        listOf(
                            NYM_RUN_STATE_KSVP_KEY to NymRunState.IDLE.name
                        )
                    )
                    callback("Failed to create new client. Retrying should work.")
                }
            }
        }
    }

    internal fun deleteClient(clientId: String, callback: () -> Unit) {
        stopNymRunWork()
        viewModelScope.launch {
            val clientsDir = getClientsDir()
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientId).deleteRecursively()
            }
            viewModelScope.launch(Dispatchers.IO) {
                keyStringValuePairRepository.remove(
                    listOf(
                        RUNNING_CLIENT_ID_KSVP_KEY,
                        RUNNING_CLIENT_ADDRESS_KSVP_KEY
                    )
                )
            }
            callback()
        }
    }
}