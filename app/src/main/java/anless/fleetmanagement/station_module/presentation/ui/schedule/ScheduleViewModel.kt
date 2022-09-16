package anless.fleetmanagement.station_module.presentation.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import anless.fleetmanagement.station_module.domain.usecase.GetCurrentScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val getCurrentScheduleUseCase: GetCurrentScheduleUseCase
) : ViewModel() {

    private val _schedule = MutableStateFlow<List<ScheduleItem>>(listOf())
    val schedule = _schedule.asStateFlow()

    private val _loading = MutableStateFlow(value = true)
    val loading = _loading.asStateFlow()

    private val _errorRes = MutableStateFlow<Int?>(value = null)
    val errorRes = _errorRes.asStateFlow()

    fun isScheduleEmpty() = _schedule.value.isEmpty()

    fun getCurrentSchedule() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            when (val result = getCurrentScheduleUseCase()) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        _schedule.value = result.data.sortedBy { it.isComplete }
                        _loading.value = false
                    }
                }
                is Result.Error -> {
                    getErrorByCode(result.errorCode)?.let { error ->
                        _errorRes.value = error
                    }
                    _loading.value = false
                }
            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null //R.string.your_session_is_closed
            else -> R.string.error_load_data
        }
}