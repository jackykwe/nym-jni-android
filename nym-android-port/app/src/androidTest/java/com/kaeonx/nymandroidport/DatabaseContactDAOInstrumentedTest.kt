package com.kaeonx.nymandroidport

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.Contact
import com.kaeonx.nymandroidport.database.ContactDAO
import com.kaeonx.nymandroidport.database.KeyStringValuePair
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
class DatabaseContactDAOInstrumentedTest {
    private lateinit var contactDAO: ContactDAO
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
        contactDAO = db.contactDao()
    }

    // Executed after each test
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getAllBySelectedClientInitiallyReturnsEmptyList() = runTest {
        val list = contactDAO.getAllBySelectedClient().first()
        assertThat(list, equalTo(listOf()))
    }

    @Test
    fun insertOrIgnoreForSelectedClientAddsNewContacts() = runTest {
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress2")
        val list = contactDAO.getAllBySelectedClient().first()
        assertThat(
            list, equalTo(
                listOf(
                    Contact(TEST_OWNER_ADDRESS, "newContactAddress1"),
                    Contact(TEST_OWNER_ADDRESS, "newContactAddress2")
                )
            )
        )
    }

    @Test
    fun insertOrIgnoreForSelectedClientIgnoresDuplicateContact() = runTest {
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        val list = contactDAO.getAllBySelectedClient().first()
        assertThat(list, equalTo(listOf(Contact(TEST_OWNER_ADDRESS, "newContactAddress1"))))
    }

    @Test
    fun deleteForSelectedClientReturnsCorrectNumberOfRowsDeleted() = runTest {
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress2")
        assertThat(contactDAO.deleteForSelectedClient("newContactAddressX"), equalTo(0))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddress1"), equalTo(1))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddress1"), equalTo(0))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddressY"), equalTo(0))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddress2"), equalTo(1))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddress2"), equalTo(0))
        assertThat(contactDAO.deleteForSelectedClient("newContactAddressZ"), equalTo(0))
    }

    @Test
    fun insertOrIgnoreForSelectedClientThenDeleteForSelectedClientReturnsNull() = runTest {
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
        contactDAO.insertOrIgnoreForSelectedClient("newContactAddress2")
        contactDAO.deleteForSelectedClient("newContactAddress1")
        contactDAO.deleteForSelectedClient("newContactAddress2")
        val list = contactDAO.getAllBySelectedClient().first()
        assertThat(list, equalTo(listOf()))
    }

    @Test
    fun insertOrIgnoreForSelectedClientThenDeleteForSelectedClientReturnsUndeletedItems() =
        runTest {
            contactDAO.insertOrIgnoreForSelectedClient("newContactAddress1")
            contactDAO.insertOrIgnoreForSelectedClient("newContactAddress2")
            contactDAO.deleteForSelectedClient("newContactAddress2")
            val list = contactDAO.getAllBySelectedClient().first()
            assertThat(list, equalTo(listOf(Contact(TEST_OWNER_ADDRESS, "newContactAddress1"))))
        }
}