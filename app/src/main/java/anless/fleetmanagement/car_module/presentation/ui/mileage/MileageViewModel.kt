package anless.fleetmanagement.car_module.presentation.ui.mileage

import androidx.lifecycle.ViewModel
import anless.fleetmanagement.car_module.domain.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MileageViewModel @Inject constructor() : ViewModel() {

    private var car: Car? = null

    private val _mileage = MutableStateFlow<Int?>(value = null)
    val mileage = _mileage.asStateFlow()


    fun setMileage(stringMileage: String) {
        _mileage.value = if (stringMileage.isEmpty()) null else stringMileage.toIntOrNull()
    }

    fun getMileage() = _mileage.value

    fun setCar(_car: Car) {
        car = _car
    }

    fun isMileageCorrect(): Boolean {
        if (car == null) return true

        if (_mileage.value != null && car != null) {
            return _mileage.value!! >= car!!.options.mileage
        }

        return false
    }

}