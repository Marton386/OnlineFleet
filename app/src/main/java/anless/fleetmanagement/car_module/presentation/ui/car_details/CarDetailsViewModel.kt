package anless.fleetmanagement.car_module.presentation.ui.car_details

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.usecase.car.GetCarUseCase
import anless.fleetmanagement.car_module.domain.usecase.documents.DownloadInsuranceUseCase
import anless.fleetmanagement.car_module.domain.usecase.documents.PrepareInsuranceUseCase
import anless.fleetmanagement.car_module.presentation.utils.ActionManager
import anless.fleetmanagement.car_module.presentation.utils.CarStatus
import anless.fleetmanagement.car_module.presentation.utils.Constants
import anless.fleetmanagement.car_module.presentation.utils.FileUtility
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    private val getCarUseCase: GetCarUseCase,
    private val prepareInsuranceUseCase: PrepareInsuranceUseCase,
    private val downloadInsuranceUseCase: DownloadInsuranceUseCase,
    private val fileUtility: FileUtility
) : ViewModel() {

    companion object {
        const val MINIMUM_LEFT_DAYS_TO_EXPIRE = 10
    }

    private var _idCar: Int? = null

    private var carObj: Car? = null

    private val _car = MutableStateFlow<Car?>(value = null)
    val car = _car.asStateFlow()

    private val _state = MutableStateFlow<CarDetailsState>(value = CarDetailsState.Empty)
    val state = _state.asStateFlow()

    private val _actions = MutableStateFlow<List<Int>>(value = listOf())
    val action = _actions.asStateFlow()

    private val _insuranceUri = MutableStateFlow<Uri?>(value = null)
    val insuranceUri = _insuranceUri.asStateFlow()


    private val _isCommissioningNewCarAction = MutableSharedFlow<Boolean>()
    val isCommissioningNewCarAction = _isCommissioningNewCarAction.asSharedFlow()

    private var insuranceFileType: String? = null

    fun clearCar() {
        _car.value = null
    }

    fun setIdCar(id: Int?) {
        if (id == null || id == _idCar) return

        _idCar = id

        if (isNewCar()) {
            viewModelScope.launch {
                _isCommissioningNewCarAction.emit(true)
            }
        }
    }

    fun carIsEmpty() = carObj == null

    fun isNewCar() = _idCar == Constants.ID_NEW_CAR

    fun getInsuranceFileType() = insuranceFileType

    fun getCarInfo() {
        if (isNewCar() || _idCar == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarDetailsState.Loading

            val result = getCarUseCase.invoke(
                idCar = _idCar ?: return@launch
            )

            when (result) {
                is Result.Success -> {
                    carObj = result.data
                    _car.value = result.data
                    _actions.value = getAvailableActions()
                    _state.value = CarDetailsState.Success(null)
                }
                is Result.Error -> {
                    setError(result.errorCode)
                }
            }
        }
    }

    fun getCarWithoutActions() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarDetailsState.Loading

            val result = getCarUseCase.invoke(
                idCar = _idCar ?: return@launch
            )

            when (result) {
                is Result.Success -> {
                    carObj = result.data
                    _car.value = result.data
                    _state.value = CarDetailsState.Success(null)
                }
                is Result.Error -> {
                    setError(result.errorCode)
                }
            }
        }
    }

    fun loadInsurance() {
        // if uri not null - is download already
        val carObj = _car.value ?: return

        carObj.insurance?.let { insurance ->
            viewModelScope.launch(Dispatchers.IO) {
                _state.value = CarDetailsState.Loading

                val result = prepareInsuranceUseCase(
                    filename = insurance.filename ?: return@launch
                )

                when (result) {
                    is Result.Success -> {
                        if (result.data) {
                            downloadInsurance(insurance.filename)
                        }
                    }
                    is Result.Error -> {
                        setError(result.errorCode)
                    }
                }
            }
        }
    }

    private suspend fun downloadInsurance(filename: String) {
        val result = downloadInsuranceUseCase(
            filename = filename
        )

        when (result) {
            is Result.Success -> {
                val uri = fileUtility.saveFile(
                    filename = filename,
                    byteArray = result.data
                )

                if (uri == null) {
                    _state.value = CarDetailsState.Error(R.string.cant_download_insurance)
                } else {
                    insuranceFileType = getFileType(filename)
                    _insuranceUri.value = uri
                    _state.value = CarDetailsState.Success(R.string.insurance_succesfully_upload)
                }
            }
            is Result.Error -> {
                setError(result.errorCode)
            }
        }
    }

    private fun getAvailableActions(): List<Int> {
        val carObj = _car.value ?: return listOf()

        return ActionManager.getAvailableActions(carObj)
}

    fun isExpireInsurance(): Boolean {
        val carObj = _car.value ?: return false
        val carInsurance = carObj.insurance ?: return false

        val milliseconds = 1000
        val dayInMilliseconds = 24 * 60 * 60 * milliseconds

        val expireDateMilliseconds = carInsurance.expireDate * milliseconds
        val currentDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val leftTime = expireDateMilliseconds - currentDate.timeInMillis

        return leftTime / dayInMilliseconds < MINIMUM_LEFT_DAYS_TO_EXPIRE
    }

    private fun getFileType(filename: String) =
        when(fileUtility.checkFileType(filename)) {
            FileUtility.FileType.PDF -> "application/pdf"
            FileUtility.FileType.JPG -> "image/jpeg"
            FileUtility.FileType.PNG -> "image/png"
            else -> null
        }

    fun getCurrentStatus(): Int? {
        val carObj = _car.value ?: return null
        return when (carObj.status) {
            CarStatus.UNKNOWN -> R.string.unknown
            CarStatus.LEASED_NOW -> R.string.leased_now
            CarStatus.ON_PARKING -> R.string.on_parking
            CarStatus.DECOMMISSIONED -> R.string.decommissioned
            CarStatus.TRANSPORTING -> R.string.in_relocation
            CarStatus.DELIVERY -> R.string.delivery_to_client
            CarStatus.MAINTENANCE,
            CarStatus.MAINTENANCE_BY_CLIENT -> R.string.on_maintenance
            CarStatus.SOLD -> R.string.sold
            else -> R.string.unknown
        }
    }

    private fun setError(codeError: Int) {
        getErrorByCode(codeError)?.let { resError ->
            _state.value = CarDetailsState.Error(resError)
        }
    }

    fun clearInsuranceUri() {
        _insuranceUri.value = null
    }

    fun getStateCarTitle(): Int? {
        if (carObj == null) {
            return null
        }

        val objCar = carObj ?: return null
        return if (objCar.options.isClean) R.string.clean else R.string.dirty
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null// R.string.your_session_is_closed
            else -> R.string.error_load_data
        }

    sealed class CarDetailsState {
        data class Success(val resMessage: Int?) : CarDetailsState()
        data class Error(val resError: Int) : CarDetailsState()
        object Loading : CarDetailsState()
        object Empty : CarDetailsState()
    }
}