package com.kaeonx.nymandroidport.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.repositories.MessageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext

    private val keyStringValuePairRepository =
        KeyStringValuePairRepository(
            AppDatabase.getInstance(getAppContext()).keyStringValuePairDao()
        )

    internal val selectedClientAddress = keyStringValuePairRepository.get(
        RUNNING_CLIENT_ADDRESS_KSVP_KEY
    ).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val messageRepository =
        MessageRepository(
            AppDatabase.getInstance(getAppContext()).messageDao()
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
            messageRepository.sendMessageFromSelectedClient(toAddress, message)
            callback(true)
        }
    }

    internal fun debugGenerateMessage(fromAddress: String) {
        viewModelScope.launch {
            messageRepository.debugSendMessageToSelectedClient(
                fromAddress,
                Random.nextInt().toString()
            )
        }
    }
}