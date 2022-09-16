package anless.fleetmanagement.car_module.presentation.ui.price

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PriceViewModel @Inject constructor(): ViewModel(){

    private val _price = MutableStateFlow<Int?>(value = null)
    val price = _price.asStateFlow()

    fun setPrice(price: Int?) {
        _price.value = price
    }

    fun getPrice() = _price.value
}