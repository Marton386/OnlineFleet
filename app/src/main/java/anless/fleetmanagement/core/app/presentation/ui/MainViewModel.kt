package anless.fleetmanagement.core.app.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.car_module.presentation.ui.settings.SettingsViewModel
import anless.fleetmanagement.core.utils.SettingsManagerPreferences
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.model.Shift
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authorizationManager: AuthorizationManager,
    private val settingsManagerPreferences: SettingsManagerPreferences
) : ViewModel() {

    val hash = authorizationManager.getHashLiveData()

    private var shift: Shift? = null
    private val _shiftIsOpen = MutableStateFlow<Boolean>(false)
    val shiftIsOpen = _shiftIsOpen.asStateFlow()

    private var currentDestination: Int? = null

    private val _loading = MutableStateFlow(value = true)
    val loading = _loading.asStateFlow()

    private val _currentThemeIsNight = MutableStateFlow<Boolean?>(value = null)
    val currentThemeIsNight = _currentThemeIsNight.asStateFlow()

    fun hasHash() = hash.value != null

    private var userTimezone: Int? = null


    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currentThemeIsNight.value = settingsManagerPreferences.getTheme()
        }
    }

    fun setLoading(state: Boolean) {
        _loading.value = state
    }

    private fun setShiftState(isOpen: Boolean) {
        _shiftIsOpen.value = isOpen
    }

    fun setShift(_shift: Shift?) {
        shift = _shift
        setShiftState(_shift != null)
    }

    fun setCurrentDestination(destination: Int) {
        currentDestination = destination
    }

    fun getCurrentDestination() = currentDestination

    fun getShift() = shift

    fun changeTheme(isNightTheme: Boolean) {
        _currentThemeIsNight.value = isNightTheme
        saveChangedTheme()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            authorizationManager.logout()
        }
    }

    private fun saveChangedTheme() {
        _currentThemeIsNight.value?.let { isNightTheme ->
            viewModelScope.launch (Dispatchers.IO) {
                settingsManagerPreferences.setTheme(isNightTheme)
            }
        }
    }

    fun setUserTimezone(timezone: Int?) {
        userTimezone = timezone
    }

    fun getUserTimezone() = userTimezone
}