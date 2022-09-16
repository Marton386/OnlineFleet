package anless.fleetmanagement.car_module.presentation.ui.washing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.SimpleItem
import anless.fleetmanagement.car_module.domain.usecase.car_options.GetCarWashesUseCase
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
class WashingViewModel @Inject constructor(
    private val getCarWashesUseCase: GetCarWashesUseCase
) : ViewModel() {
    private val _idCarWash = MutableSharedFlow<Int?>()
    val idCarWash = _idCarWash.asSharedFlow()

    private val _carWashes = MutableStateFlow<List<SimpleItem>?>(value = null)
    val carWashes = _carWashes.asStateFlow()

    private val _loading = MutableStateFlow(value = true)
    val loading = _loading.asStateFlow()

    private val _errorRes = MutableSharedFlow<Int>()
    val errorRes = _errorRes.asSharedFlow()

    init {
        getCarWashes()
    }

    fun setIdCarWash(id: Int) {
        viewModelScope.launch {
            _idCarWash.emit(id)
        }
    }

    fun getCarWashes() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val result = getCarWashesUseCase()

            when (result) {
                is Result.Success -> {
                    _carWashes.value = result.data
                    _loading.value = false
                }
                is Result.Error -> {
                    _loading.value = false
                    _errorRes.emit(getErrorByCode(result.errorCode))
                }
            }
            withContext(Dispatchers.Main) {

            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> R.string.your_session_is_closed
            else -> R.string.error_load_data
        }
}