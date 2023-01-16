package com.kaeonx.nymandroidport.repositories

//import com.kaeonx.nymchatprototype.database.Message
//import com.kaeonx.nymchatprototype.database.MessageDAO
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.distinctUntilChanged
//
///*
// * Reminder: use distinctUntilChanged()
// * Observable queries in Room have one important limitation: the query reruns whenever any row
// * in the table is updated, whether or not that row is in the result set. You can ensure that
// * the UI is only notified when the actual query results change by applying the
// * distinctUntilChanged() operator at the observation site.
// * <https://developer.android.com/training/data-storage/room/async-queries#observable>
// */
//class MessageRepository(private val messageDAO: MessageDAO) {
//    internal fun getMessagesWithSelectedClient(contactAddress: String): Flow<List<Message>> {
//        return messageDAO.getAllWithSelectedClient(contactAddress).distinctUntilChanged()
//    }
//
//    internal fun getEarliestPendingSendFromSelectedClient(): Flow<Message?> {
//        return messageDAO.getEarliestPendingSendFromSelectedClient()
//    }
//
//    internal suspend fun updateEarliestPendingSendById(id: Int) {
//        return messageDAO.updateEarliestPendingSendById(id)
//    }
//
//    internal suspend fun sendMessageFromSelectedClient(toAddress: String, message: String) {
//        messageDAO.insertFromSelectedClient(toAddress, message)
//    }
//
////    internal suspend fun debugSendMessageToSelectedClient(fromAddress: String, message: String) {
////        messageDAO.insertToSelectedClient(fromAddress, message)
////    }
//}