package com.kaeonx.nymandroidport.datasources

//import com.kaeonx.nymandroidport.database.Contact
//import com.kaeonx.nymandroidport.database.ContactDAO
//import com.kaeonx.nymandroidport.database.Message
//import com.kaeonx.nymandroidport.database.MessageDAO
//import kotlinx.coroutines.CoroutineDispatcher
//import kotlinx.coroutines.flow.Flow
//
//class NymMessageLocalDataSource(
//    private val contactDAO: ContactDAO,
//    private val messageDAO: MessageDAO,
//    private val ioDispatcher: CoroutineDispatcher  // for execution of suspend functions
//) {
//    internal fun getContacts(receiverNymId: String): Flow<List<Contact>> {
//        return contactDAO.getAll(receiverNymId)
//    }
//
//    internal fun getMessages(senderNymId: String, receiverNymId: String): Flow<List<Message>> {
//        return messageDAO.getAllBySender(senderNymId, receiverNymId)
//    }
//}