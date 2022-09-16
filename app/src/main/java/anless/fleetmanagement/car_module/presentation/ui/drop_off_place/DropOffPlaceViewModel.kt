package anless.fleetmanagement.car_module.presentation.ui.drop_off_place

import androidx.lifecycle.ViewModel
import anless.fleetmanagement.car_module.presentation.utils.DropOffPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DropOffPlaceViewModel @Inject constructor(): ViewModel(){

    private val _dropOffPlace = MutableStateFlow<DropOffPlace?>(value = null)
    val dropOffPlace = _dropOffPlace.asStateFlow()

    fun setDropOffPlace(place: DropOffPlace) {
        _dropOffPlace.value = place
    }

    fun getDropOffPlace() = _dropOffPlace.value
}