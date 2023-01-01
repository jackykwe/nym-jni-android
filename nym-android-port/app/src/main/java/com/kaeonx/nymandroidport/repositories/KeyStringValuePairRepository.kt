package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.KeyStringValuePair
import com.kaeonx.nymandroidport.database.KeyStringValuePairDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/*
 * Reminder: use distinctUntilChanged()
 * Observable queries in Room have one important limitation: the query reruns whenever any row
 * in the table is updated, whether or not that row is in the result set. You can ensure that
 * the UI is only notified when the actual query results change by applying the
 * distinctUntilChanged() operator at the observation site.
 * <https://developer.android.com/training/data-storage/room/async-queries#observable>
 */
class KeyStringValuePairRepository(private val keyStringValuePairDAO: KeyStringValuePairDAO) {
    internal fun get(key: String): Flow<String?> {
        return keyStringValuePairDAO.get(key).map { it?.value }.distinctUntilChanged()
    }

    /**
     * For each key in `keys`, the output map will map the key to:
     * - a String value if there exists a value stored for the key
     * - `null` otherwise.
     */
    internal fun get(keys: List<String>): Flow<Map<String, String?>> {
        return keyStringValuePairDAO.get(keys).map { keyStringValuePairs ->
            keys.associateWith { key -> keyStringValuePairs.find { it.key == key }?.value }
        }.distinctUntilChanged()
    }

    /**
     * This is always performed as a transaction, in the case that `keyStringValuePairs` contains
     * more than 1 item.
     */
    internal suspend fun put(keyStringValuePairs: List<Pair<String, String>>) {
        keyStringValuePairDAO.upsert(keyStringValuePairs.map {
            // Avoid exposing implementation detail of KeyStringValuePair to users of the repository
            KeyStringValuePair(
                key = it.first,
                value = it.second
            )
        })
    }

    internal suspend fun remove(keys: List<String>) {
        keyStringValuePairDAO.delete(keys)
    }
}