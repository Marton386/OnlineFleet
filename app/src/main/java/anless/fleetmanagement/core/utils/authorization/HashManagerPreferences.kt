package anless.fleetmanagement.core.utils.authorization

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.asLiveData
import anless.fleetmanagement.core.utils.PreferencesKeys
import anless.fleetmanagement.user_module.domain.model.UserHash
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HashManagerPreferences(
    private val dataStore: DataStore<Preferences>
) {

    private val dataStoreKey = stringPreferencesKey(PreferencesKeys.USER_HASH)

    suspend fun setHash(userHash: UserHash) {
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = userHash.hash
        }
    }

    suspend fun getHash(): String? {
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    suspend fun clearHash() {
        dataStore.edit { preferences ->
            preferences.remove(dataStoreKey)
        }
    }

    fun getHashLiveData() = dataStore.data.map { it[dataStoreKey] }.asLiveData()

}