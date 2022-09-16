package anless.fleetmanagement.car_module.presentation.ui.invoice

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentInvoiceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class InvoiceFragment: Fragment(R.layout.fragment_invoice) {

    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInvoiceBinding.bind(view)

        AndroidUtils.openKeyboard(binding.etInvoiceNumber)

        binding.etInvoiceNumber.addTextChangedListener { text ->
            invoiceViewModel.setInvoiceNumber(text.toString().trim())
        }

        binding.btnNext.setOnClickListener {
            invoiceViewModel.getInvoiceNumber()?.let { invoiceNumber ->
                carActionViewModel.setInvoiceNumber(invoiceNumber)
            }
        }

        carActionViewModel.getActionTitle()?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi()
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            invoiceViewModel.invoiceNumber.collectLatest { invoiceNumber ->
                binding.btnNext.isEnabled = !invoiceNumber.isNullOrEmpty()
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}