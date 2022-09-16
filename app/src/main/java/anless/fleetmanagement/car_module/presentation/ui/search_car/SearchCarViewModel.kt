package anless.fleetmanagement.car_module.presentation.ui.search_car

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.usecase.car.SearchCarUseCase
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchCarViewModel @Inject constructor(
    private val searchCarUseCase: SearchCarUseCase
) : ViewModel() {

    companion object {
        const val TAG = "SearchCarViewModel"
        const val MIN_SYMBOLS_SEARCH_CAR = 3
        const val MIN_SYMBOLS_FULL_LICENSE_PLATE = 7
    }

    private val _searchPart = MutableStateFlow("")

    private val _car = MutableStateFlow<CarItem?>(value = null)
    val car = _car.asStateFlow()

    private val _cars = MutableStateFlow<List<CarItem>>(listOf())
    val cars = _cars.asStateFlow()

    private var carsList: List<CarItem> = listOf()

    private val _loading = MutableStateFlow<Boolean>(value = false)
    val loading = _loading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage = _errorMessage.asSharedFlow()

    val readyAddNewCar: StateFlow<Boolean> =
        combine(_searchPart, _cars, _loading) { _, _, isLoading ->
            !isLoading && carNoFound() && isCorrectLicensePlate()
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)


    private fun carNoFound() = _searchPart.value.isNotBlank() && carsList.isEmpty()

    private fun clearCars() {
        carsList = listOf()
        viewModelScope.launch {
            _cars.emit(listOf())
        }
    }

    fun selectCar(car: CarItem) {
        _car.value = car
    }

    fun isCarSelected() = _car.value != null

    fun unselectCar() {
        _car.value = null
    }

    fun setSearchPart(part: String) {
        _searchPart.value = part
        if (!isLengthEnoughForSearch())
            clearCars()
        else
            searchCars()
    }

    fun getSearchPart() = _searchPart.value

    private fun isCorrectLicensePlate() =
        _searchPart.value.trim().length >= MIN_SYMBOLS_FULL_LICENSE_PLATE

    fun isLengthEnoughForSearch() = _searchPart.value.length >= MIN_SYMBOLS_SEARCH_CAR

    private fun searchCars() {
        _car.value = null
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val result = searchCarUseCase(
                partLicensePlate = _searchPart.value
            )

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        //_cars.value = result.data
                        viewModelScope.launch {
                            _cars.emit(result.data)
                        }
                        carsList = result.data
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

    private fun setError(codeError: Int) {
        viewModelScope.launch {
            getErrorByCode(codeError)?.let { resError ->
                _errorMessage.emit(resError)
            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null // R.string.your_session_is_closed
            else -> R.string.error_load_data
        }
}