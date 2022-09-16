package anless.fleetmanagement.car_module.presentation.ui.tire_options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.TireParams
import anless.fleetmanagement.car_module.domain.usecase.car_options.GetCurrentTiresUseCase
import anless.fleetmanagement.car_module.presentation.utils.TireOptions
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TireOptionsViewModel @Inject constructor(
    private val getCurrentTiresUseCase: GetCurrentTiresUseCase
) : ViewModel() {

    private var idCar: Int? = null

    private val _currentTireType = MutableStateFlow<TireParams.Type?>(value = null)
    val currentTireType = _currentTireType.asStateFlow()

    private val _loading = MutableStateFlow(value = true)
    val loading = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<Int?>(value = null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _newTireType = MutableStateFlow<TireParams.Type?>(value = null)
    private val _tireCondition = MutableStateFlow<TireParams.Condition?>(value = null)
    private val _tireProfile = MutableStateFlow(value = TireOptions.Profile.MIN_VALUE)
    private val _tireWidth = MutableStateFlow(value = TireOptions.Width.MIN_VALUE)
    private val _tireDiameter = MutableStateFlow(value = TireOptions.Diameter.MIN_VALUE)

    val tireParamsReady: StateFlow<Boolean> =
        combine(
            _newTireType,
            _tireCondition
        ) { type, condition ->
            type != null && condition != null
        }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)

    fun getTireParams() = TireParams(
        type = _newTireType.value!!,
        condition = _tireCondition.value!!,
        profile = _tireProfile.value,
        width = _tireWidth.value,
        diameter = _tireDiameter.value
    )

    fun setTireType(type: TireParams.Type) {
        _newTireType.value = type
    }

    fun setTireCondition(condition: TireParams.Condition) {
        _tireCondition.value = condition
    }

    fun setWidth(count: Int) {
        val width = count * TireOptions.Width.STEP + TireOptions.Width.MIN_VALUE
        _tireWidth.value = width
    }

    fun setProfile(count: Int) {
        val profile = count * TireOptions.Profile.STEP + TireOptions.Profile.MIN_VALUE
        _tireProfile.value = profile
    }

    fun setDiameter(diameter: Int) {
        _tireDiameter.value = diameter
    }

    fun getWidthValues(): Array<String> {
        val amount: Int =
            (TireOptions.Width.MAX_VALUE - TireOptions.Width.MIN_VALUE) / TireOptions.Width.STEP + 1

        var values = Array(amount) { i ->
            (TireOptions.Width.MIN_VALUE + i * TireOptions.Width.STEP).toString()
        }

        return values
    }

    fun getProfileValues(): Array<String> {
        val amount: Int =
            (TireOptions.Profile.MAX_VALUE - TireOptions.Profile.MIN_VALUE) / TireOptions.Profile.STEP + 1

        var values = Array(amount) { i ->
            (TireOptions.Profile.MIN_VALUE + i * TireOptions.Profile.STEP).toString()
        }

        return values
    }

    fun setIdCar(_idCar: Int) {
        idCar = _idCar
        getCurrentTireType()
    }

    fun getCurrentTireType() {
        if (idCar == null) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (!_loading.value) {
                _loading.value = true
            }

            val result = getCurrentTiresUseCase(idCar!!)

            when (result) {
                is Result.Success -> {
                    _loading.value = false
                    _currentTireType.value = result.data
                }
                is Result.Error -> {
                    _loading.value = false
                    setError(result.errorCode)
                }
            }

            withContext(Dispatchers.Main) {}
        }
    }

    private fun setError(codeError: Int) {
        getErrorByCode(codeError)?.let { resError ->
            _errorMessage.value = resError
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null //R.string.your_session_is_closed
            //ErrorCodes.NOT_FOUND -> R.string.error_load_data
            else -> R.string.error_load_data
        }
}