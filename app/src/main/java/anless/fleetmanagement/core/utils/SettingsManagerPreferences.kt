package anless.fleetmanagement.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class SettingsManagerPreferences(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val DARK_THEME_YES = "darkTheme_yes"
        private val DARK_THEME_NO = "darkTheme_no"
    }

    private val dataStoreAutoLogoutKey = stringPreferencesKey(PreferencesKeys.AUTO_LOGOUT)
    private val dataStoreThemeKey = stringPreferencesKey(PreferencesKeys.DARK_THEME)

    suspend fun setAutoLogoutState(autoLogout: Boolean) {
        dataStore.edit { preferences ->
            preferences[dataStoreAutoLogoutKey] = autoLogout.toString()
        }
    }

    suspend fun getAutoLogoutState(): Boolean {
        val preferences = dataStore.data.first()
        return preferences[dataStoreAutoLogoutKey].toBoolean()
    }

    suspend fun setTheme(isNightTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[dataStoreThemeKey] = isNightTheme.toString()
        }
    }

    suspend fun getTheme(): Boolean? {
        val preferences = dataStore.data.first()
        val isNightTheme = preferences[dataStoreThemeKey]
        return isNightTheme?.toBoolean()
    }
}