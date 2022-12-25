package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.Application
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.nymRun
import com.kaeonx.nymandroidport.jni.topLevelInit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

private const val TAG = "clientInfoViewModel"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getStorageAbsPath() = getAppContext().filesDir.absolutePath
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    private val _selectedClient: MutableStateFlow<String?> = MutableStateFlow(null)
    val selectedClient: StateFlow<String?>
        get() = _selectedClient
    private val _clients = listOf<String>().toMutableStateList()
    val clients: List<String>
        get() = _clients.sorted()

    private var runningClient: Job? = null

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
                // sets up logging on Rust side; will not re-run on device rotation (activity recreation)
                // (re-running causes crash)
                topLevelInit()
            }
            refreshClientsList()
        }
    }

    // TODO NEXT: NEED TO MAKE IT STOPPABLE
    // COROUTINES ARE NOT THE RIGHT WAY TO HANDLE PERSISTENT JOBS
    private suspend fun runClient(clientName: String) {
        runningClient?.cancelAndJoin()
        runningClient = viewModelScope.launch(Dispatchers.Default) {
            nymRun(getStorageAbsPath(), clientName)
        }
        _selectedClient.value = clientName
    }

    internal fun selectClient(clientName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            runClient(clientName)  // will launch another coroutine
        }
    }

    private suspend fun stopRunningClient() {
        runningClient?.cancelAndJoin()
        runningClient = null
        _selectedClient.value = null
    }

    internal fun unselectClient() {
        Log.e(TAG, "STOPPING")
        viewModelScope.launch(Dispatchers.Default) {
            stopRunningClient()
        }
    }

    internal fun addClient(newClientName: String, callback: (Boolean) -> Unit) {
        val nonEmptyNewClientName = newClientName.ifEmpty { "client" }

        if (clients.contains(nonEmptyNewClientName)) {
            // Already exists, just run
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    runClient(nonEmptyNewClientName)  // will launch another coroutine
                }
                callback(true)
            }
        } else {
            // Create new
            viewModelScope.launch {
                try {
                    withContext(Dispatchers.Default) {
                        nymInit(getStorageAbsPath(), nonEmptyNewClientName)
                        runClient(nonEmptyNewClientName)  // will launch another coroutine
                    }
                    refreshClientsList()
                    callback(true)
                } catch (e: RuntimeException) {
                    e.printStackTrace()
                    callback(false)
                }
            }
        }
    }

    internal fun deleteClient(clientName: String, callback: () -> Unit) {
        val clientsDir = getClientsDir()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                stopRunningClient()
            }
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientName).deleteRecursively()
            }
            refreshClientsList()
            callback()
        }
    }


}