package anless.fleetmanagement.car_module.presentation.ui.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Reservation
import anless.fleetmanagement.car_module.domain.model.ReservationItem
import anless.fleetmanagement.car_module.domain.model.ReservationSearch
import anless.fleetmanagement.car_module.domain.usecase.car_options.GetReservationsUseCase
import anless.fleetmanagement.car_module.presentation.ui.maitenance.MaintenanceViewModel
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
class ReservationViewModel @Inject constructor(
    private val getReservationsUseCase: GetReservationsUseCase
) : ViewModel() {

    companion object {
        const val TAG = "ReservationViewModel"
    }

    private var idCar: Int? = null
    private val _reservationNumber = MutableStateFlow<String?>(value = null)
    val reservationNumber = _reservationNumber.asStateFlow()

    private val _reservations = MutableSharedFlow<List<ReservationItem>>()
    val reservations = _reservations.asSharedFlow()
/*
    private val _reservation = MutableStateFlow<Reservation?>(value = null)
    val reservation = _reservation.asStateFlow()  */

    private val _reservation = MutableSharedFlow<Reservation>()
    val reservation = _reservation.asSharedFlow()

    private val _loading = MutableStateFlow(value = false)
    val loading = _loading.asStateFlow()

    private val _errorRes = MutableSharedFlow<Int>()
    val errorRes = _errorRes.asSharedFlow()

    fun setIdCar(id: Int) {
        idCar = id
    }

    fun setResNumber(resNumber: String) {
        _reservationNumber.value = resNumber
    }

    fun clearReservations() {
        viewModelScope.launch {
            _reservations.emit(listOf())
        }
    }

    fun getResNumber() = _reservationNumber.value

    //fun reservationsFound() = _reservations.value?.isNotEmpty() ?: false

    fun setReservation(res: ReservationItem) {
        if (res.error != null) {
            viewModelScope.launch {
                setError(res.error)
            }
            return
        }

        viewModelScope.launch {
            _reservation.emit(Reservation(
                resNumber = res.resNumber,
                idSource = res.source.id
            ))
        }
    }

    fun searchReservations() {
        if (_reservationNumber.value == null || idCar == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val result = getReservationsUseCase(
                reservationSearch = ReservationSearch(
                    resNumber = _reservationNumber.value ?: return@launch,
                    idCar = idCar ?: return@launch
                )
            )

            withContext(Dispatchers.IO) {
                when (result) {
                    is Result.Success -> {
                        _reservations.emit(result.data)
                        _loading.value = false
                    }
                    is Result.Error -> {
                        setError(result.errorCode)
                        _loading.value = false
                    }
                }
            }
        }
    }

/*    private fun setErrorMessages(reservations: List<ReservationItem>) =
        reservations.forEach { res ->
            if (res.error != null) {
                res.error.id = res.error.id
            }
        }*/

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