package anless.fleetmanagement.car_module.presentation.ui.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.CarPosition
import anless.fleetmanagement.car_module.domain.usecase.car_options.GetCarPositionUseCase
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val getCarPositionUseCase: GetCarPositionUseCase
) : ViewModel() {

    companion object {
        const val TAG = "MonitoringViewModel"
        const val UPDATING_INTERVAL_MILLIS = 60 * 1000L
        const val DEFAULT_MAP_ZOOM = 14f
    }

    private var idCar: Int? = null
    private var mapZoom = DEFAULT_MAP_ZOOM

    private val _carPosition = MutableStateFlow<CarPosition?>(value = null)
    val carPosition = _carPosition.asStateFlow()

    private val _loading = MutableStateFlow(value = false)
    val loading = _loading.asStateFlow()

    private val _errorRes = MutableStateFlow<Int?>(value = null)
    val errorRes = _errorRes.asStateFlow()

    private val _carWithoutEquipment = MutableStateFlow<Boolean?>(value = null)
    val carWithoutEquipment = _carWithoutEquipment.asStateFlow()

    private var repeatJob: Job? = null

    init {
        mapZoom = DEFAULT_MAP_ZOOM
    }

    fun setCarId(id: Int) {
        idCar = id
        startJob()
    }

    fun setMapZoom(zoom: Float) {
        mapZoom = zoom
    }


    fun getMapZoom() = mapZoom

    fun getCarPosition() {
        startJob()
    }


    private fun startJob() {
        viewModelScope.launch {
            if (repeatJob != null) {
                repeatJob?.cancel()
            }

            repeatJob = startRepeatingJob()
        }
    }

    private fun startRepeatingJob(): Job {
        return viewModelScope.launch {
            while (isActive) {
                updateCarPosition()
                delay(UPDATING_INTERVAL_MILLIS)
            }
        }
    }

    private fun updateCarPosition() {
        if (idCar == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true

            val result = getCarPositionUseCase(
                idCar = idCar ?: return@launch
            )

            when (result) {
                is Result.Success -> {
                    _carPosition.emit(result.data)
                    _carPosition.value = result.data
                    _loading.value = false
                }
                is Result.Error -> {
                    val errorCode = result.errorCode
                    if (errorCode == ErrorCodes.WRONG_CAR) {
                        _carWithoutEquipment.value = true
                    } else {
                        setError(errorCode)
                    }
                    _loading.value = false
                    stopUpdatingCarPosition()
                }
            }
        }
    }

    fun clearData() {
        stopUpdatingCarPosition()
        onCleared()
    }

    private fun stopUpdatingCarPosition() {
        viewModelScope.launch {
            repeatJob?.cancel()
            repeatJob = null
        }
    }

    private fun setError(codeError: Int) {
        getErrorByCode(codeError)?.let { resError ->
            _errorRes.value = resError
        }
    }

    fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null //R.string.check_login_and_pass
            ErrorCodes.WRONG_CAR -> R.string.not_available_for_this_vehicle
            else -> R.string.error_load_data
        }


}