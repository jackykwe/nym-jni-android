package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.Contact
import com.kaeonx.nymandroidport.database.ContactDAO
import kotlinx.coroutines.flow.Flow

// You should create a repository class for each different type of data you handle in your app.
// Repositories abstract sources of data from the rest of the app, making things easier to test.
// <https://developer.android.com/topic/architecture#data-layer>
// Also, the data layer should expose suspend functions and Flows, instead of launching coroutines.
// The launch of coroutines is done in the UI layer (ViewModel).
// <https://developer.android.com/kotlin/coroutines/coroutines-best-practices#coroutines-data-layer>
// TODO (clarify): I inject the DAO directly into the repository, as the DataSource pattern seems to be an additional layer of unnecessary, repeated indirection.
// <https://developer.android.com/topic/architecture/data-layer#room_as_a_data_source>
class ContactRepository(private val contactDAO: ContactDAO) {
    internal fun getContactsOfSelectedClient(): Flow<List<Contact>> {
        return contactDAO.getBySelectedClient()
    }

    internal suspend fun addContact(newContactAddress: String) {
        contactDAO.insertForSelectedClient(newContactAddress)
    }
}