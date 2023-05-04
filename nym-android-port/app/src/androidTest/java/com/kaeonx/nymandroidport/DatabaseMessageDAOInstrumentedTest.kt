package com.kaeonx.nymandroidport

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.KeyStringValuePair
import com.kaeonx.nymandroidport.database.Message
import com.kaeonx.nymandroidport.database.MessageDAO
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

private const val TEST_OWNER_ADDRESS = "clientAddress"

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)  // to use the runTest coroutine scope
class DatabaseMessageDAOInstrumentedTest {
    private lateinit var messageDAO: MessageDAO
    private lateinit var db: AppDatabase

    // Executed before each test
    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        db.keyStringValuePairDao().upsert(
            listOf(
                KeyStringValuePair(RUNNING_CLIENT_ADDRESS_KSVP_KEY, TEST_OWNER_ADDRESS),
            )
        )
        messageDAO = db.messageDao()
    }

    // Executed after each test
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getAllWithSelectedClientInitiallyReturnsEmptyList() = runTest {
        val list = messageDAO.getAllWithSelectedClient("contactAddress").first()
        assertThat(list, equalTo(listOf()))
    }

    @Test
    fun insertFromSelectedClientInsertsMessage() = runTest {
        val id1 = messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress1")
        val id2 = messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress2")
        val list = messageDAO.getAllWithSelectedClient("contactAddress").first()
        assertThat(
            list,
            equalTo(
                listOf(
                    // Highest ID on top for prototyping purposes only
                    // (For typically messaging applications, highest ID would be on the bottom)
                    Message(
                        id2.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress2",
                        false
                    ),
                    Message(
                        id1.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress1",
                        false
                    )
                )
            )
        )
    }

    @Test
    fun updateEarliestPendingSendByIdMarksMessageAsSent() = runTest {
        val id1 = messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress1")
        val id2 = messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress2")
        messageDAO.updateEarliestPendingSendById(id2.toInt())
        val list = messageDAO.getAllWithSelectedClient("contactAddress").first()
        assertThat(
            list,
            equalTo(
                listOf(
                    Message(
                        id2.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress2",
                        true
                    ),
                    Message(
                        id1.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress1",
                        false
                    )
                )
            )
        )
    }

    @Test
    fun getEarliestPendingSendFromSelectedClientInitiallyReturnsNull() = runTest {
        val message = messageDAO.getEarliestPendingSendFromSelectedClient().first()
        assertThat(message, equalTo(null))
    }

    @Test
    fun getEarliestPendingSendFromSelectedClientReturnsEarliestEnqueuedMessageThatIsNotSent() =
        runTest {
            val id1 =
                messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress1")
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(
                    Message(
                        id1.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress1",
                        false
                    )
                )
            )
            val id2 =
                messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress2")
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(
                    Message(
                        id1.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress1",
                        false
                    )
                )
            )
            val id3 =
                messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress3")
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(
                    Message(
                        id1.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress1",
                        false
                    )
                )
            )

            messageDAO.updateEarliestPendingSendById(id1.toInt())
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(
                    Message(
                        id2.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress2",
                        false
                    )
                )
            )

            messageDAO.updateEarliestPendingSendById(id3.toInt())
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(
                    Message(
                        id2.toInt(),
                        TEST_OWNER_ADDRESS,
                        "contactAddress",
                        "messageToContactAddress2",
                        false
                    )
                )
            )

            messageDAO.updateEarliestPendingSendById(id2.toInt())
            assertThat(
                messageDAO.getEarliestPendingSendFromSelectedClient().first(),
                equalTo(null)
            )
        }

    @Test
    fun insertToSelectedClientInsertsMessage() = runTest {
        val id1 = messageDAO.insertToSelectedClient("contactAddress", "messageFromContactAddress1")
        val id2 = messageDAO.insertToSelectedClient("contactAddress", "messageFromContactAddress2")
        val list = messageDAO.getAllWithSelectedClient("contactAddress").first()
        assertThat(
            list,
            equalTo(
                listOf(
                    Message(
                        id2.toInt(),
                        "contactAddress",
                        TEST_OWNER_ADDRESS,
                        "messageFromContactAddress2",
                        true
                    ),
                    Message(
                        id1.toInt(),
                        "contactAddress",
                        TEST_OWNER_ADDRESS,
                        "messageFromContactAddress1",
                        true
                    )
                )
            )
        )
    }

    @Test
    fun deleteBetweenSelectedClientAndContactReturnsCorrectNumberOfRowsDeleted() = runTest {
        assertThat(messageDAO.deleteBetweenSelectedClientAndContact("contactAddress"), equalTo(0))
        messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress1")
        messageDAO.insertToSelectedClient("contactAddress", "messageFromContactAddress1")
        assertThat(messageDAO.deleteBetweenSelectedClientAndContact("contactAddress"), equalTo(2))
    }

    @Test
    fun deleteBetweenSelectedClientAndContactDeletesAllMessagesBetweenSelectedClientAndContact() =
        runTest {
            messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress1")
            messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress2")
            messageDAO.insertFromSelectedClient("contactAddress", "messageToContactAddress3")
            messageDAO.insertToSelectedClient("contactAddress", "messageFromContactAddress1")
            messageDAO.insertToSelectedClient("contactAddress", "messageFromContactAddress2")
            messageDAO.deleteBetweenSelectedClientAndContact("contactAddress")
            val list = messageDAO.getEarliestPendingSendFromSelectedClient().first()
            assertThat(list, equalTo(null))
        }
}