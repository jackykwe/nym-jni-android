package com.kaeonx.nymandroidport

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.KeyStringValuePair
import com.kaeonx.nymandroidport.database.KeyStringValuePairDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)  // to use the runTest coroutine scope
class DatabaseKeyStringValuePairDAOInstrumentedTest {
    private lateinit var keyStringValuePairDAO: KeyStringValuePairDAO
    private lateinit var db: AppDatabase

    // Executed before each test
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        keyStringValuePairDAO = db.keyStringValuePairDao()
    }

    // Executed after each test
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun getReturnsNullIfKeyNotFound() = runTest {
        val pair = keyStringValuePairDAO.get("key").first()
        assertThat(pair, equalTo(null))
    }

    @Test
    fun getLatestReturnsNullIfKeyNotFound() = runTest {
        val pair = keyStringValuePairDAO.getLatest("key")
        assertThat(pair, equalTo(null))
    }

    @Test
    fun getReturnsEmptyListIfKeyNotFound() = runTest {
        val list = keyStringValuePairDAO.get(listOf("key")).first()
        assertThat(list, equalTo(listOf()))
    }

    @Test
    fun upsertThenGetReturnsPair() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value1"),
                KeyStringValuePair("key2", "value2")
            )
        )
        val pair1 = keyStringValuePairDAO.get("key1").first()
        val pair2 = keyStringValuePairDAO.get("key2").first()
        assertThat(pair1, not(equalTo(null)))
        assertThat(pair1!!.key, equalTo("key1"))
        assertThat(pair1.value, equalTo("value1"))
        assertThat(pair2, not(equalTo(null)))
        assertThat(pair2!!.key, equalTo("key2"))
        assertThat(pair2.value, equalTo("value2"))
    }

    @Test
    fun upsertThenGetLatestReturnsPair() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value1"),
                KeyStringValuePair("key2", "value2")
            )
        )
        val pair1 = keyStringValuePairDAO.getLatest("key1")
        val pair2 = keyStringValuePairDAO.getLatest("key2")
        assertThat(pair1, not(equalTo(null)))
        assertThat(pair1!!.key, equalTo("key1"))
        assertThat(pair1.value, equalTo("value1"))
        assertThat(pair2, not(equalTo(null)))
        assertThat(pair2!!.key, equalTo("key2"))
        assertThat(pair2.value, equalTo("value2"))
    }

    @Test
    fun upsertThenGetReturnsPairList() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value1"),
                KeyStringValuePair("key2", "value2"),
            )
        )
        val list = keyStringValuePairDAO.get(listOf("key1", "key2", "key3")).first()
        assertThat(list.size, equalTo(2))
        assertThat(list[0].key, equalTo("key1"))
        assertThat(list[0].value, equalTo("value1"))
        assertThat(list[1].key, equalTo("key2"))
        assertThat(list[1].value, equalTo("value2"))
    }

    @Test
    fun upsertReplacesValue() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key", "value")
            )
        )
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key", "newValue")
            )
        )
        val pair = keyStringValuePairDAO.get("key").first()
        assertThat(pair, not(equalTo(null)))
        assertThat(pair!!.key, equalTo("key"))
        assertThat(pair.value, equalTo("newValue"))
    }

    @Test
    fun upsertThenDeleteReturnsCorrectNumberOfRowsDeleted() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value2"),
                KeyStringValuePair("key2", "value2"),
            )
        )
        assertThat(keyStringValuePairDAO.delete(listOf("key3", "key4", "key5")), equalTo(0))
        assertThat(keyStringValuePairDAO.delete(listOf("key1")), equalTo(1))
        assertThat(keyStringValuePairDAO.delete(listOf("key1")), equalTo(0))
        assertThat(keyStringValuePairDAO.delete(listOf("key2")), equalTo(1))
        assertThat(keyStringValuePairDAO.delete(listOf("key2")), equalTo(0))
    }

    @Test
    fun upsertThenDeleteReturnsNull() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value2"),
                KeyStringValuePair("key2", "value2"),
            )
        )
        val rowsDeleted = keyStringValuePairDAO.delete(listOf("key1", "key2"))
        assertThat(rowsDeleted, equalTo(2))
        val list = keyStringValuePairDAO.get(listOf("key1", "key2")).first()
        assertThat(list, equalTo(listOf()))
    }

    @Test
    fun upsertTwoItemsThenDeleteOneReturnsPairListWithUndeletedItem() = runTest {
        keyStringValuePairDAO.upsert(
            listOf(
                KeyStringValuePair("key1", "value1"),
                KeyStringValuePair("key2", "value2"),
            )
        )
        val rowsDeleted = keyStringValuePairDAO.delete(listOf("key1"))
        assertThat(rowsDeleted, equalTo(1))
        val pair = keyStringValuePairDAO.get("key2").first()
        assertThat(pair, not(equalTo(null)))
        assertThat(pair!!.key, equalTo("key2"))
        assertThat(pair.value, equalTo("value2"))
    }
}