package anless.fleetmanagement.car_module.presentation.ui.stop_days

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentStopDaysBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StopDaysFragment: Fragment(R.layout.fragment_stop_days) {

    companion object {
        const val TAG = "StopDaysFragment"
    }

    private val stopDaysViewModel: StopDaysViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentStopDaysBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStopDaysBinding.bind(view)

        val values = stopDaysViewModel.getValues()
        val minValue = stopDaysViewModel.getMinDays()
        binding.numpDays.minValue = minValue
        binding.numpDays.maxValue = values.size
        binding.numpDays.displayedValues = values
        binding.numpDays.value = minValue

        binding.numpDays.setOnValueChangedListener { picker, oldVal, newVal ->
            stopDaysViewModel.setAmountDays(newVal)
        }

        binding.btnNext.setOnClickListener {
            carActionViewModel.setDaysAmount(stopDaysViewModel.getDaysAmount())
        }

        val resTitle = carActionViewModel.getActionTitle()
        resTitle?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi()
    }

    private fun subscribeUi(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
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