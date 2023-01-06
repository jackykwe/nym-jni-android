package com.kaeonx.nymandroidport.ui.screens.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ID_KSVP_KEY
import com.kaeonx.nymandroidport.repositories.ContactRepository
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//private const val TAG = "contactsViewModel"

// Lasts as long as the app, isn't cleared when navigating away from ContactsScreen
class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext

    private val keyStringValuePairRepository =
        KeyStringValuePairRepository(
            AppDatabase.getInstance(getAppContext()).keyStringValuePairDao()
        )

    private val contactRepository =
        ContactRepository(
            AppDatabase.getInstance(getAppContext()).contactDao()
        )

    internal val contactsScreenUIState =
        combine(
            keyStringValuePairRepository.get(RUNNING_CLIENT_ID_KSVP_KEY),  // Flow
            contactRepository.getContactsOfSelectedClient()  // Flow
        ) { selectedClientId, contacts ->
            ContactsScreenUIState(
                selectedClientId = selectedClientId,
                contactAddresses = contacts.map { it.contactAddress }
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            ContactsScreenUIState(
                selectedClientId = null,
                contactAddresses = listOf()
            )
        )

    internal fun addContact(newContactNymAddress: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            contactRepository.addContactOfSelectedClient(
                newContactAddress = newContactNymAddress
            )
            callback(true)  // must be called on main thread
        }
    }
}