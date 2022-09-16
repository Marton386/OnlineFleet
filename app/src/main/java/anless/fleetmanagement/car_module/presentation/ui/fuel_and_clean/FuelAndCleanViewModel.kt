package anless.fleetmanagement.car_module.presentation.ui.fuel_and_clean

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.data.utils.CleanState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FuelAndCleanViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val TAG = "FuelViewModel"
        private val FUEL_SCALES_ARRAY = arrayListOf(0, 12, 25, 37, 50, 62, 75, 87, 100)
        private const val START_SCALE_INDEX = 0
    }

    private val _fuel = MutableStateFlow<Int?>(value = FUEL_SCALES_ARRAY[START_SCALE_INDEX])
    val fuel = _fuel.asStateFlow()

    private val _imageRes = MutableStateFlow<Int?>(value = getImageScaleRes(START_SCALE_INDEX))
    val imageRes = _imageRes.asStateFlow()

    private var cleanState: Int? = null

    fun setFuel(index: Int){
        _fuel.value = FUEL_SCALES_ARRAY[index]
        getImageScaleRes(index)?.let { imgRes ->
            _imageRes.value = imgRes
        }
    }

    fun getFuel() = fuel.value

    fun setCleanState(state: Int) {
        cleanState = state
    }

    fun getCleanState() = cleanState

    private fun getImageScaleRes(index: Int) =
        when (index) {
            // load only one time?
            0 -> R.drawable.ic_fuel_scale_0
            1 -> R.drawable.ic_fuel_scale_1
            2 -> R.drawable.ic_fuel_scale_2
            3 -> R.drawable.ic_fuel_scale_3
            4 -> R.drawable.ic_fuel_scale_4
            5 -> R.drawable.ic_fuel_scale_5
            6 -> R.drawable.ic_fuel_scale_6
            7 -> R.drawable.ic_fuel_scale_7
            8 -> R.drawable.ic_fuel_scale_8
            else -> null
        }
}