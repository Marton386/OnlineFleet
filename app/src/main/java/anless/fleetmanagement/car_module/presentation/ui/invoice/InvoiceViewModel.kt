package anless.fleetmanagement.car_module.presentation.ui.invoice

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
) : ViewModel() {

    private val _invoiceNumber = MutableStateFlow<String?>(value = null)
    val invoiceNumber = _invoiceNumber.asStateFlow()

    fun setInvoiceNumber(numberInvoice: String) {
        _invoiceNumber.value = numberInvoice
    }

    fun getInvoiceNumber() = _invoiceNumber.value
}