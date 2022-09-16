package anless.fleetmanagement.user_module.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo
import anless.fleetmanagement.user_module.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _login = MutableStateFlow<String>(value = "")
    val login = _login.asStateFlow()

    private val _password = MutableStateFlow<String>(value = "")
    val password = _password.asStateFlow()

    private val _state = MutableStateFlow<LoginState>(LoginState.Empty)
    val state = _state.asStateFlow()

    private val _loadingState: StateFlow<Boolean> = _state.map { curState ->
        curState is LoginState.Loading
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)

    val readyToLogin: StateFlow<Boolean> =
        combine(_login, _password, _loadingState) { loginValue, passValue, loading ->
            loginValue.isNotBlank() && passValue.isNotBlank() && !loading
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)


    fun loginUser(versionCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = LoginState.Loading
            val result = userUseCases.createSessionUseCase(
                UserLoginInfo(
                    login = _login.value,
                    password = _password.value,
                    appVersion = versionCode
                )
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _state.emit(LoginState.Success(result.data))
                    }
                    is Result.Error -> {
                        _state.emit(LoginState.Error(getErrorByCode(result.errorCode)))
                    }
                }
            }
        }
    }

    fun setLogin(login: String) {
        _login.value = login
    }

    fun setPassword(pass: String) {
        _password.value = pass
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> R.string.check_login_and_pass
            else -> R.string.error_load_data
        }

    sealed class LoginState {
        data class Success(val userHash: UserHash) : LoginState()
        data class Error(val codeError: Int) : LoginState()
        object Loading : LoginState()
        object Empty : LoginState()
    }
}