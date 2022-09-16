package anless.fleetmanagement.car_module.presentation.ui.stop_days

import androidx.lifecycle.ViewModel
import anless.fleetmanagement.car_module.presentation.utils.CarStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StopDaysViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val MIN_DAYS = 1
        const val MAX_DAYS = 90
        const val LAST_VALUE = CarStatus.SOLD
    }

    private val _daysAmount = MutableStateFlow(value = MIN_DAYS)
    val daysAmount = _daysAmount.asStateFlow()

    fun getMinDays() = MIN_DAYS

    fun setAmountDays(days: Int) {
        _daysAmount.value = if (days <= MAX_DAYS) days else LAST_VALUE
    }

    fun getDaysAmount() = _daysAmount.value

    fun getValues(): Array<String> {
        var values = Array(MAX_DAYS + 1) { i ->
            (i + 1).toString()
        }
        values[MAX_DAYS] = LAST_VALUE.toString()

        return values
    }
}