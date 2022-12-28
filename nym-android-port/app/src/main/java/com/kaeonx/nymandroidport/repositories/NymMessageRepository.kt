package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.datasources.NymMessageRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// You should create a repository class for each different type of data you handle in your app.
// Repositories abstract sources of data from the rest of the app, making things easier to test.
// <https://developer.android.com/topic/architecture#data-layer>
// Also, the data layer should expose suspend functions and Flows, instead of launching coroutines.
// The launch of coroutines is done in the UI layer (ViewModel).
// <https://developer.android.com/kotlin/coroutines/coroutines-best-practices#coroutines-data-layer>
class NymMessageRepository(
    private val nymMessageRemoteDataSource: NymMessageRemoteDataSource  // TODO: DATABASE. Offline-first support.
) {

    // TODO: Make message a class that contains more information; interact with dataSource
    suspend fun sendMessage(message: String) {

    }

    // TODO: Make message a class that contains more information; interact with dataSource
    fun getMessages(): Flow<String> {
        return flowOf("Test")
    }
}