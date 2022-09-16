package anless.fleetmanagement.user_module.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.usecase.CheckUpdateAppRequireUseCase
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.SettingsManagerPreferences
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.model.Shift
import anless.fleetmanagement.user_module.domain.model.ShiftLocation
import anless.fleetmanagement.user_module.domain.model.User
import anless.fleetmanagement.user_module.domain.usecase.UserUseCases
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val checkUpdateRequiredUseCase: CheckUpdateAppRequireUseCase,
    private val authorizationManager: AuthorizationManager,
    private val settingsManagerPreferences: SettingsManagerPreferences
) : ViewModel() {

    companion object {
        const val TAG = "ProfileViewModel"
    }

    private val _user = MutableStateFlow<User?>(value = null)
    val user = _user.asStateFlow()

    private val _shift = MutableStateFlow<Shift?>(value = null)
    val shift = _shift.asStateFlow()

    private val _state = MutableStateFlow<ProfileState>(value = ProfileState.Empty)
    val state = _state.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(value = null)
    val location = _location.asStateFlow()

    private val _idStation = MutableStateFlow<Int?>(null)
    var idStation = _idStation.asStateFlow()

    private var needCloseSession: Boolean = false

    private val _requiredUpdateApp = MutableSharedFlow<Boolean>()
    val requiredUpdateApp = _requiredUpdateApp.asSharedFlow()

    init {
    }

    fun setLocation(location: LatLng) {
        _location.value = location
    }

    fun setIdStation(id: Int) {
        _idStation.value = id
        openShift()
    }

    fun isShiftOpen() = _shift.value != null

    fun isUserEmpty() = _user.value == null

    fun isSessionNeedClose() = needCloseSession

    fun sessionClosed() {
        needCloseSession = false
    }

    fun getUserTimezone() = _user.value?.timezone

    fun checkSession() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(ProfileState.Loading)
            when (val result = userUseCases.checkSessionUseCase()) {
                is Result.Success -> {
                    needCloseSession = false
                    if (_user.value == null) {
                        getUser()
                    } else {
                        getCurrentShift()
                    }
                }
                is Result.Error -> {
                    needCloseSession = true
                    withContext(Dispatchers.Main) {
                        getErrorByCode(result.errorCode)?.let { errorRes ->
                            _state.value = ProfileState.Error(errorRes)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getUser() {
        when (val result = userUseCases.getUserUseCase()) {
            is Result.Success -> {
                _user.value = result.data
                getCurrentShift()
            }
            is Result.Error -> {
                withContext(Dispatchers.Main) {
                    getErrorByCode(result.errorCode)?.let { errorRes ->
                        _state.value = ProfileState.Error(errorRes)

                    }
                }
            }
        }
    }

    private suspend fun getCurrentShift() {
        val result = userUseCases.getCurrentShiftUseCase()
        withContext(Dispatchers.Main) {
            when (result) {
                is Result.Success -> {
                    _shift.value = result.data
                    _state.value = ProfileState.Success(result.data)

                }
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        if (result.errorCode == ErrorCodes.NOT_FOUND) {
                            _shift.value = null
                            _state.value = ProfileState.Success(null)
                        } else {
                            getErrorByCode(result.errorCode)?.let { errorRes ->
                                _state.value = ProfileState.Error(errorRes)

                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun openShift() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userUseCases.openShiftUseCase(
                shiftLocation = ShiftLocation(
                    locationLatLng = _location.value ?: return@launch,
                    idStation = _idStation.value ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    _idStation.value = null
                    _location.value = null
                    getCurrentShift()
                }
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        getErrorByCode(result.errorCode)?.let { errorRes ->
                            _state.value = ProfileState.Error(errorRes)

                        }
                    }
                }
            }
        }
    }

    fun closeShift() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userUseCases.closeShiftUseCase(
                location = _location.value ?: return@launch
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _shift.value = null
                        _location.value = null
                        _state.value = ProfileState.Success(null)

                        val requireAutoLogout = settingsManagerPreferences.getAutoLogoutState()
                        if (requireAutoLogout) {
                            authorizationManager.logout()
                        }
                    }
                    is Result.Error -> {
                        getErrorByCode(result.errorCode)?.let { errorRes ->
                            _state.value = ProfileState.Error(errorRes)
                        }
                    }
                }
            }
        }
    }

    fun checkRequireUpdateApp() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = checkUpdateRequiredUseCase()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _requiredUpdateApp.emit(result.data)
                    }
                    is Result.Error -> {
                    }
                }
            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null//R.string.your_session_is_closed
            else -> R.string.error_load_data
        }


    sealed class ProfileState {
        data class Success(val shift: Shift?) : ProfileState()
        data class Error(val codeError: Int) : ProfileState()
        object Loading : ProfileState()
        object Empty : ProfileState()
    }
}