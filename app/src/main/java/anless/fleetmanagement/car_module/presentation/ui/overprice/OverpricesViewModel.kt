package anless.fleetmanagement.car_module.presentation.ui.overprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.car_module.domain.usecase.car_params.CheckOverpricesUseCase
import anless.fleetmanagement.car_module.presentation.ui.maitenance.MaintenanceViewModel
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OverpricesViewModel @Inject constructor(
    private val checkOverpricesUseCase: CheckOverpricesUseCase
) : ViewModel() {

    private var idCar: Int? = null
    private var carParam: CarParam? = null

/*    private val _overprices = MutableSharedFlow<Overprices>()
    val overprices = _overprices.asSharedFlow()*/

    private val _overprices = MutableStateFlow<Overprices?>(value = null)
    val overprices = _overprices.asStateFlow()

    private val _loading = MutableStateFlow(value = false)
    val loading = _loading.asStateFlow()

/*    private val _errorRes = MutableSharedFlow<Int>()
    val errorRes = _errorRes.asSharedFlow()*/

    private val _errorRes = MutableStateFlow<Int?>(value = null)
    val errorRes = _errorRes.asStateFlow()

    fun setCarInfo(_idCar: Int, _carParam: CarParam) {
        idCar = _idCar
        carParam = _carParam
        checkOverprices()
    }

    private fun isCarInfoReady() = (idCar != null || carParam != null)

    fun checkOverprices() {
        if (!isCarInfoReady()) {
           /* viewModelScope.launch {
                _errorRes.emit(R.string.error_load_data)
            }*/
            _errorRes.value = R.string.error_load_data
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true

            val result = checkOverpricesUseCase(
                idCar = idCar ?: return@launch,
                carParam = carParam ?: return@launch
            )

            when (result) {
                is Result.Success -> {
                    //_overprices.emit(result.data)
                    _overprices.value = result.data
                    _loading.value = false
                }
                is Result.Error -> {
                    setError(result.errorCode)
                    _loading.value = false
                }
            }
        }
    }

    private fun setError(codeError: Int) {
/*        viewModelScope.launch {
            getErrorByCode(codeError)?.let { resError ->
                _errorRes.emit(resError)
            }
        }*/
        getErrorByCode(codeError)?.let { resError ->
            _errorRes.value = resError
        }
    }

    fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null//R.string.check_login_and_pass
            else -> R.string.error_load_data
        }
}