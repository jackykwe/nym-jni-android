package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.Message
import com.kaeonx.nymandroidport.database.MessageDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/*
 * Reminder: use distinctUntilChanged()
 * Observable queries in Room have one important limitation: the query reruns whenever any row
 * in the table is updated, whether or not that row is in the result set. You can ensure that
 * the UI is only notified when the actual query results change by applying the
 * distinctUntilChanged() operator at the observation site.
 * <https://developer.android.com/training/data-storage/room/async-queries#observable>
 */
// TODO: Websocket interaction to push messages into database (both from others and from "me")
class MessageRepository(private val messageDAO: MessageDAO) {
    internal fun getMessagesWithSelectedClient(contactAddress: String): Flow<List<Message>> {
        return messageDAO.getAllWithSelectedClient(contactAddress).distinctUntilChanged()
    }

    // TODO: Make message a class that contains more information; interact with dataSource
    internal suspend fun sendMessageFromSelectedClient(toAddress: String, message: String) {
        messageDAO.insertFromSelectedClient(toAddress, message)
    }

    // TODO remove debug functions
    internal suspend fun debugSendMessageToSelectedClient(fromAddress: String, message: String) {
        messageDAO.debugInsertToSelectedClient(fromAddress, message)
    }
}