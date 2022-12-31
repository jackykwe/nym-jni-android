package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.MessageDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MessageRepository(private val messageDAO: MessageDAO) {
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