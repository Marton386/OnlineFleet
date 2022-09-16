package anless.fleetmanagement.car_module.presentation.ui.relocations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Relocation
import anless.fleetmanagement.car_module.domain.usecase.car_options.GetRelocationsUseCase
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RelocationsViewModel @Inject constructor(
    private val getRelocationsUseCase: GetRelocationsUseCase
) : ViewModel() {

    private val _relocations = MutableStateFlow<List<Relocation>?>(value = null)
    val relocations = _relocations.asStateFlow()

    private val _loading = MutableStateFlow(value = true)
    val loading = _loading.asStateFlow()

    /*    private val _errorRes = MutableSharedFlow<Int>()
        val errorRes = _errorRes.asSharedFlow()*/
    private val _errorRes = MutableStateFlow<Int?>(value = null)
    val errorRes = _errorRes.asStateFlow()

    init {
        checkRelocations()
    }

    fun clearRelocations() {
        _relocations.value = null
    }

    fun hasNoRelocations(): Boolean {
        _relocations.value?.let { relocationsList ->
            return relocationsList.isEmpty()
        }

        return false
    }

    fun getRelocations() = relocations.value

    fun checkRelocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val result = getRelocationsUseCase()
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _loading.value = false
                        _relocations.value = result.data
                    }
                    is Result.Error -> {
                        _loading.value = false
                        setError(result.errorCode)
                    }
                }
            }
        }
    }

    private fun setError(codeError: Int) {
        /*  viewModelScope.launch {
              getErrorByCode(codeError)?.let { resError ->
                  _errorRes.emit(resError)
              }
          }*/
        getErrorByCode(codeError)?.let { resError ->
            _errorRes.value = resError
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null //R.string.your_session_is_closed
            else -> R.string.error_load_data
        }
}