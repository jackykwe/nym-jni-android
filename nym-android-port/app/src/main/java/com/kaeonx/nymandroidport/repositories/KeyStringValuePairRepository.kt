package com.kaeonx.nymandroidport.repositories

import com.kaeonx.nymandroidport.database.KeyStringValuePair
import com.kaeonx.nymandroidport.database.KeyStringValuePairDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KeyStringValuePairRepository(private val keyStringValuePairDAO: KeyStringValuePairDAO) {
    internal fun get(key: String): Flow<String?> {
        return keyStringValuePairDAO.get(key).map { it?.value }
    }

    /**
     * For each key in `keys`, the output map will map the key to:
     * - a String value if there exists a value stored for the key
     * - `null` otherwise.
     */
    internal fun get(keys: List<String>): Flow<Map<String, String?>> {
        return keyStringValuePairDAO.get(keys).map { keyStringValuePairs ->
            keys.associateWith { key -> keyStringValuePairs.find { it.key == key }?.value }
        }
    }

    // Convenience function
    internal suspend fun put(key: String, value: String) {
        keyStringValuePairDAO.upsert(listOf(KeyStringValuePair(key = key, value = value)))
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

    internal suspend fun remove(key: String) {
        keyStringValuePairDAO.delete(key)
    }

    internal suspend fun remove(keys: List<String>) {
        keyStringValuePairDAO.delete(keys)
    }
}