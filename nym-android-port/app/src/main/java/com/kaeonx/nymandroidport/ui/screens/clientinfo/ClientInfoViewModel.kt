package com.kaeonx.nymandroidport.ui.screens.clientinfo

import android.app.Application
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.jni.nymInit
import com.kaeonx.nymandroidport.jni.topLevelInit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//private const val TAG = "clientInfoViewModel"

class ClientInfoViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel.
    private fun getAppContext() = getApplication<Application>().applicationContext
    private fun getClientsDir() = getAppContext().filesDir
        .toPath().resolve(".nym").resolve("clients")
        .toFile()

    private val _selectedClient: MutableStateFlow<String?> = MutableStateFlow(null)
    val selectedClient: StateFlow<String?>
        get() = _selectedClient
    private val _clients = listOf<String>().toMutableStateList()
    val clients: List<String>
        get() = _clients.sorted()

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

    /**
     * If `<none>` is to be shown on screen, provide `clientName == null`
     */
    fun setSelectedClient(clientName: String?) {
        _selectedClient.value = clientName
    }

    fun addClient(newClientName: String, callback: (Boolean) -> Unit) {
        val nonEmptyNewClientName = newClientName.ifEmpty { "client" }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                nymInit(getAppContext().filesDir.absolutePath, nonEmptyNewClientName)
                refreshClientsList()
                setSelectedClient(nonEmptyNewClientName)
                callback(true)
            } catch (e: RuntimeException) {
                e.printStackTrace()
                callback(false)
            }
        }
    }

    fun deleteClient(clientName: String, callback: () -> Unit) {
        val clientsDir = getClientsDir()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clientsDir.resolve(clientName).deleteRecursively()
            }
            refreshClientsList()
            setSelectedClient(null)
            callback()
        }
    }
}