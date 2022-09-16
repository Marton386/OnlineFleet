package anless.fleetmanagement.car_module.presentation.ui.liters

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LitresViewModel @Inject constructor(): ViewModel() {

    companion object {
        private const val MIN_LITRES = 1
        private const val MAX_LITRES = 150
        private const val DEFAULT_LITRES = 20
    }
    private val _litres = MutableStateFlow(value = DEFAULT_LITRES)
    val litres = _litres.asStateFlow()

    fun getMinLitres() = MIN_LITRES
    fun getMaxLitres() = MAX_LITRES
    fun getDefaultLitres() = DEFAULT_LITRES

    fun setLitres(litres: Int) {
        _litres.value = litres
    }

    fun getLitres() = _litres.value
}