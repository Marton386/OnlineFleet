package anless.fleetmanagement.car_module.presentation.ui.contractor

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContractorViewModel @Inject constructor(
): ViewModel() {

    private val _contractor = MutableStateFlow<String?> (value = null)
    val contractor = _contractor.asStateFlow()

    fun setContractor(contractor: String) {
        _contractor.value = contractor
    }

    fun getContractor() = _contractor.value
}