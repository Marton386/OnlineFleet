package anless.fleetmanagement.car_module.presentation.ui.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.car_module.domain.usecase.car.GetContractUseCase
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
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
class QrViewModel @Inject constructor(
    private val getContractUseCase: GetContractUseCase
) : ViewModel() {
    private val _contract = MutableStateFlow<LightContract?>(value = null)
    val contract = _contract.asStateFlow()
    var contractId: Int? = null

    private val _errorRes = MutableSharedFlow<Int>()
    val errorRes = _errorRes.asSharedFlow()

    fun clearAll() {
        _contract.value = null
    }

    fun searchContract(idContract: String) {
        viewModelScope.launch(Dispatchers.IO) {
            contractId = idContract.toInt()
            val result = getContractUseCase(idContract)
            withContext(Dispatchers.IO) {
                when (result) {
                    is Result.Success -> {
                        _contract.emit(result.data)
                    }
                    is Result.Error -> {
                        setError(result.errorCode)
                    }
                }
            }
        }
    }

    private fun setError(codeError: Int) {
        viewModelScope.launch {
            getErrorByCode(codeError)?.let { resError ->
                _errorRes.emit(resError)
            }
        }
    }

    fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null // R.string.check_login_and_pass
            ErrorCodes.WRONG_CAR -> R.string.wrong_car_for_reservation
            ErrorCodes.NOT_ACCEPTABLE -> R.string.not_allowed_for_this_shift
            ErrorCodes.NOT_FOUND -> R.string.reservation_not_found
            ErrorCodes.CAR_ISSUED -> R.string.car_issued
            else -> R.string.error_load_data
        }
}