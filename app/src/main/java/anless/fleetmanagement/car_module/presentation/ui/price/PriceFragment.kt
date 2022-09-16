package anless.fleetmanagement.car_module.presentation.ui.price

import android.os.Bundle
import android.view.View
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
import anless.fleetmanagement.databinding.FragmentPriceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PriceFragment : Fragment(R.layout.fragment_price) {

    companion object {
        private const val TAG = "PriceFragment"
    }

    private val priceViewModel: PriceViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentPriceBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPriceBinding.bind(view)

        AndroidUtils.openKeyboard(binding.etPrice)

        binding.etPrice.addTextChangedListener { _price ->
            val priceText = _price.toString().trim()
            val price = if (priceText.isNotEmpty()) {
                priceText.toInt()
            } else {
                null
            }

            priceViewModel.setPrice(price)
        }

        binding.btnNext.setOnClickListener {
            priceViewModel.getPrice()?.let { price ->
                carActionViewModel.setPrice(price)
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
            priceViewModel.price.collectLatest { price ->
                binding.btnNext.isEnabled = (price != null)
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