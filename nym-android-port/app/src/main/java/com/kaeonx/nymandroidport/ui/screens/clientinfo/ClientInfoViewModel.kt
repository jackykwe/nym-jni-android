package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.Application
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ID_KSVP_KEY
import com.kaeonx.nymandroidport.jni.getAddress
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.services.NYMRUN_FOREGROUND_SERVICE_CLIENT_ID_EXTRA_KEY
import com.kaeonx.nymandroidport.services.NYM_RUN_PORT
import com.kaeonx.nymandroidport.services.NymRunForegroundService
import com.kaeonx.nymandroidport.utils.NymRunState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private const val TAG = "clientInfoViewModel"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    ///////////////////////////////
    // BASIC VIEWMODEL FUNCTIONS //
    ///////////////////////////////

    // DONE: Other fields store reference to this leakable object; It's OK, lasts till END of app. Problem is with activityContext.
    private val applicationContext = application
    private fun getClientsDir() = applicationContext.filesDir.resolve(".nym").resolve("clients")

    ////////////////////////////////////////////////
    // REPOSITORIES, FOREGROUND SERVICE AND FLOWS //
    ////////////////////////////////////////////////
    private suspend fun getClientsList(): List<String> {
        return withContext(Dispatchers.IO) {
            getClientsDir().list()  // null if clientsDir doesn't exist (app run for very first time after install)
                ?.asList()
                ?: listOf()
        }
    }

    private val keyStringValuePairRepository = KeyStringValuePairRepository(
        AppDatabase.getInstance(applicationContext).keyStringValuePairDao()
    )

    // For debugging only: if state machine misbehaves
    init {
        runBlocking {
            keyStringValuePairRepository.put(listOf(NYM_RUN_STATE_KSVP_KEY to NymRunState.IDLE.name))
        }
    }

    // Always up-to-date values in the database; hot flow
    // https://developer.android.com/kotlin/flow/stateflow-and-sharedflow#stateflow
    // The Flow.stateIn() pattern is an alternative to the default pattern described in the link
    // above, as mentioned here:
    // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/
    internal val clientInfoScreenUIState = keyStringValuePairRepository.get(
        listOf(
            NYM_RUN_STATE_KSVP_KEY,
            RUNNING_CLIENT_ID_KSVP_KEY,
            RUNNING_CLIENT_ADDRESS_KSVP_KEY,
        )
    ).map {
        Log.w(
            TAG,
            "clientInfoScreenUIState changed | " +
                    "nym run state is now ${it[NYM_RUN_STATE_KSVP_KEY]}; " +
                    "running client ID is now ${it[RUNNING_CLIENT_ID_KSVP_KEY]}; " +
                    "running client address is now ${it[RUNNING_CLIENT_ADDRESS_KSVP_KEY]}"
        )
        ClientInfoScreenUIState(
            clients = getClientsList(),
            selectedClientId = it[RUNNING_CLIENT_ID_KSVP_KEY],
            selectedClientAddress = it[RUNNING_CLIENT_ADDRESS_KSVP_KEY],
            nymRunState = NymRunState.valueOf(it[NYM_RUN_STATE_KSVP_KEY] ?: NymRunState.IDLE.name)
        )
    }.stateIn(  // turn cold flow into hot flow
        viewModelScope,
        SharingStarted.Eagerly,
        ClientInfoScreenUIState(
            clients = listOf(),
            selectedClientId = null,
            selectedClientAddress = null,
            nymRunState = NymRunState.IDLE
        )
    )

    // TODO, if important
//    private suspend fun fixBrokenState() {
//    }

    // Only run once, when ClientInfoScreen is launched and so the ClientInfoViewModel is created
    // (lasts across activity recreation)
    init {
        // coroutines are launched on UI thread (Dispatches.Main) by default
        viewModelScope.launch(Dispatchers.Default) {
            // sets up logging on Rust side
            topLevelInit(applicationContext.filesDir.absolutePath)
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

    internal fun startNymRunForegroundService() {
        Log.i(TAG, "enqueuing work...")
        viewModelScope.launch {
            Log.i(TAG, "enqueuing work... (inside coroutine Main/UI)")
            withContext(Dispatchers.IO) {
                Log.i(TAG, "enqueuing work... (inside coroutine, inside withContext IO)")
                keyStringValuePairRepository.put(
                    listOf(
                        NYM_RUN_STATE_KSVP_KEY to NymRunState.SETTING_UP.name
                    )
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(
                    Intent(
                        applicationContext,
                        NymRunForegroundService::class.java
                    ).apply {
                        putExtra(
                            NYMRUN_FOREGROUND_SERVICE_CLIENT_ID_EXTRA_KEY,
                            clientInfoScreenUIState.value.selectedClientId!!
                        )
                    }
                )
            } else {
                applicationContext.startService(
                    Intent(
                        applicationContext,
                        NymRunForegroundService::class.java
                    ).apply {
                        putExtra(
                            NYMRUN_FOREGROUND_SERVICE_CLIENT_ID_EXTRA_KEY,
                            clientInfoScreenUIState.value.selectedClientId!!
                        )
                    }
                )
            }
        }
    }

    internal fun stopNymRunForegroundService() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                keyStringValuePairRepository.put(
                    listOf(NYM_RUN_STATE_KSVP_KEY to NymRunState.TEARING_DOWN.name)
                )
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
                    callback(null)  // no error; must be called on main thread
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    callback("Failed to create new client. Retrying should work.")  // must be called on main thread
                }
            }
        }
    }

    internal fun deleteClient(clientId: String, callback: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val clientsDir = getClientsDir()
                clientsDir.resolve(clientId).deleteRecursively()
                keyStringValuePairRepository.remove(
                    listOf(
                        RUNNING_CLIENT_ID_KSVP_KEY,
                        RUNNING_CLIENT_ADDRESS_KSVP_KEY
                    )
                )
            }
            callback()  // must be called on main thread
        }
    }
}