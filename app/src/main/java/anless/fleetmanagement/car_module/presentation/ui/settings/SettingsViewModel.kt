package anless.fleetmanagement.car_module.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.core.utils.SettingsManagerPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManagerPreferences: SettingsManagerPreferences
) : ViewModel() {

    private val _autoLogout = MutableStateFlow(value = false)
    val autoLogout = _autoLogout.asStateFlow()

    init {
        getAutoLogoutState()
    }

    fun setAutoLogoutState(autoLogoutState: Boolean) {
        _autoLogout.value = autoLogoutState
        saveAutoLogoutState()
    }

    private fun getAutoLogoutState(){
        viewModelScope.launch(Dispatchers.IO) {
            _autoLogout.value = settingsManagerPreferences.getAutoLogoutState()
        }
    }

    private fun saveAutoLogoutState() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManagerPreferences.setAutoLogoutState(_autoLogout.value)
        }
    }
}