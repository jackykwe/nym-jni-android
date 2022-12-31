package com.kaeonx.nymandroidport.ui.screens.contacts

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.nymandroidport.database.Contact
import com.kaeonx.nymandroidport.jni.getAddress
import com.kaeonx.nymandroidport.repositories.NymRepository
import com.kaeonx.nymandroidport.ui.screens.clientinfo.SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "contactsViewModel"

// Lasts as long as the app, isn't cleared when navigating away from ContactsScreen
class ContactsViewModel(
    private val nymRepository: NymRepository,
    application: Application
) : AndroidViewModel(application) {
    // This is a leakable object, so only generate when needed, and GC when done. Therefore,
    // not stored as a persistent field in an AndroidViewModel (can cause leak). Instead, it is
    // guarded behind a function call.
    private fun getAppContext() = getApplication<Application>().applicationContext

    // Shared Preferences to store selected client name
    private fun getSharedPref() = getAppContext().getSharedPreferences(
        getAppContext().getString(
            com.kaeonx.nymandroidport.R.string.preference_file_key
        ),
        Context.MODE_PRIVATE
    )

    private suspend fun getSelectedClientAddress(): String? {
        return getSharedPref().getString(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY, null)?.let {
            withContext(Dispatchers.Default) {
                getAddress(it)
            }
        }
    }

    private var _contacts = MutableStateFlow<List<Contact>>(listOf()).asStateFlow()
    internal val contacts: StateFlow<List<Contact>>
        get() = _contacts

    private suspend fun updateContactsStateFlow() {
        val selectedClientAddress = getSelectedClientAddress()
        _contacts = if (selectedClientAddress == null) {
            MutableStateFlow<List<Contact>>(listOf()).asStateFlow()
        } else {
            // stateIn() converts cold Flow into hot StateFlow, which is expected by composables (TODO: clarify)
            nymRepository
                .getContacts(selectedClientAddress = selectedClientAddress)
                .stateIn(viewModelScope)
        }
    }

    init {
        viewModelScope.launch {
            updateContactsStateFlow()
        }
    }

    internal fun addContact(newContactNymId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val selectedClientAddress = withContext(Dispatchers.Default) {
                getSelectedClientAddress()
            } ?: return@launch callback(false)

            withContext(Dispatchers.IO) {
                nymRepository.addContact(
                    selectedClientAddress = selectedClientAddress,
                    newContactAddress = newContactNymId
                )
            }

            callback(true)
        }
    }

//    internal suspend fun getContacts(): StateFlow<List<Contact>> {
//        val activeNymId = getSharedPref().getString(SELECTED_CLIENT_NAME_SHARED_PREFERENCE_KEY, null)
//        return if (activeNymId == null) {
//            Log.w(TAG, "returning empty flow")
//            (listOf())
//        } else {
//            Log.w(TAG, "returning actual flow")
//            // Turning cold Flows into hot StateFlows <https://developer.android.com/kotlin/flow/stateflow-and-sharedflow#sharein>
//            nymRepository.getContacts(activeNymId).distinctUntilChanged().stateIn(viewModelScope)
//        }
//    }

}