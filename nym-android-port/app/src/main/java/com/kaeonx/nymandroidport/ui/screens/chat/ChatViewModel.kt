package com.kaeonx.nymandroidport.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.repositories.MessageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val TAG = "chatViewModel"

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    // DONE: Other fields store reference to this leakable object; It's OK, lasts till END of app. Problem is with activityContext.
    private val applicationContext = application

    private val appDatabaseInstance = AppDatabase.getInstance(applicationContext)
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

//    internal fun debugGenerateMessage(fromAddress: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            messageRepository.debugSendMessageToSelectedClient(
//                fromAddress,
//                Random.nextInt().toString()
//            )
//        }
//    }

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

    // For data collection
//    init {
//        viewModelScope.launch(Dispatchers.IO) {
//            while (true) {
//                delay(1000L)
//                messageRepository.sendMessageFromSelectedClient(
//                    selectedClientAddress.value!!,
//                    System.currentTimeMillis().toString()
//                )
//            }
//        }
//    }
}