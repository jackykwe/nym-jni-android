package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.Contact
import com.kaeonx.nymandroidport.database.ContactDAO
import com.kaeonx.nymandroidport.database.MessageDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// You should create a repository class for each different type of data you handle in your app.
// Repositories abstract sources of data from the rest of the app, making things easier to test.
// <https://developer.android.com/topic/architecture#data-layer>
// Also, the data layer should expose suspend functions and Flows, instead of launching coroutines.
// The launch of coroutines is done in the UI layer (ViewModel).
// <https://developer.android.com/kotlin/coroutines/coroutines-best-practices#coroutines-data-layer>
// TODO (clarify): I inject the DAO directly into the repository, as the DataSource pattern seems to be an additional layer of unnecessary, repeated indirection.
// <https://developer.android.com/topic/architecture/data-layer#room_as_a_data_source>
class NymRepository(
    private val contactDAO: ContactDAO,
    private val messageDAO: MessageDAO,
) {
    internal fun getContacts(selectedClientAddress: String): Flow<List<Contact>> {
        return contactDAO.getAll(selectedClientAddress)
    }

    internal suspend fun addContact(selectedClientAddress: String, newContactAddress: String) {
        contactDAO.insert(
            Contact(
                ownerAddress = selectedClientAddress,
                contactAddress = newContactAddress
            )
        )
    }

    // TODO: Make message a class that contains more information; interact with dataSource
    internal suspend fun sendMessage(message: String) {
        // use externalCoroutineScope
//        externalCoroutineScope.async {
//            // TODO
//        }.await()
    }

    // TODO: Make message a class that contains more information; interact with dataSource
    internal fun getMessages(): Flow<String> {
        return flowOf("Test")
    }
}