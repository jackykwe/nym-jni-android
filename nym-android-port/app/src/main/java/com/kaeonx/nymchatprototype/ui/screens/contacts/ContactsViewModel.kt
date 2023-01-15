package com.kaeonx.nymchatprototype.ui.screens.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymchatprototype.database.AppDatabase
import com.kaeonx.nymchatprototype.database.RUNNING_CLIENT_ID_KSVP_KEY
import com.kaeonx.nymchatprototype.repositories.ContactRepository
import com.kaeonx.nymchatprototype.repositories.KeyStringValuePairRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//private const val TAG = "contactsViewModel"

// Lasts as long as the app, isn't cleared when navigating away from ContactsScreen
class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    // DONE: Other fields store reference to this leakable object; It's OK, lasts till END of app. Problem is with activityContext.
    private val applicationContext = application

    private val keyStringValuePairRepository =
        KeyStringValuePairRepository(
            AppDatabase.getInstance(applicationContext).keyStringValuePairDao()
        )

    private val contactRepository =
        ContactRepository(
            AppDatabase.getInstance(applicationContext).contactDao()
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
            contactRepository.addIfNotExistsContactOfSelectedClient(
                newContactAddress = newContactNymAddress
            )
            callback(true)  // must be called on main thread
        }
    }
}