package com.kaeonx.nymandroidport.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.repositories.MessageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Other fields store reference to this leakable object
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext

    private val appDatabaseInstance = AppDatabase.getInstance(getAppContext())
    private val keyStringValuePairRepository =
        KeyStringValuePairRepository(
            appDatabaseInstance.keyStringValuePairDao()
        )

    internal val selectedClientAddress = keyStringValuePairRepository.get(
        RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val messageRepository =
        MessageRepository(
            appDatabaseInstance.messageDao()
        )

    private val _contactAddress = MutableStateFlow("")
    internal fun initContactAddress(contactAddress: String) {
        _contactAddress.value = contactAddress
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    internal val messages = _contactAddress.flatMapLatest {
        messageRepository.getMessagesWithSelectedClient(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, listOf())

    internal fun sendMessage(toAddress: String, message: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                messageRepository.sendMessageFromSelectedClient(toAddress, message)
            }
            callback(true)  // must be called on main thread
        }
    }

    internal fun debugGenerateMessage(fromAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.sendMessageToSelectedClient(
                fromAddress,
                Random.nextInt().toString()
            )
        }
    }

    internal fun deleteContact(contactAddress: String, callback: () -> Unit) {
        viewModelScope.launch {
            // TODO (clarify): is there a more sensible way to do this? Kinda of bypassed the repository here..
            withContext(Dispatchers.IO) {
                appDatabaseInstance.run {
                    withTransaction {
                        messageDao().deleteBetweenSelectedClientAndContact(contactAddress)
                        contactDao().deleteForSelectedClient(contactAddress)
                    }
                }
            }
            callback()  // must be called on main thread
        }
    }
}