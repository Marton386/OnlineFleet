package anless.fleetmanagement.car_module.presentation.ui.fuel_and_clean

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.data.utils.CleanState
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentFuelAndCleanBinding
import com.devadvance.circularseekbar.CircularSeekBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

class FuelAndCleanFragment : Fragment(R.layout.fragment_fuel_and_clean) {

    companion object {
        const val TAG = "FuelAndStateFragment"
    }

    private val fuelAndCleanViewModel: FuelAndCleanViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentFuelAndCleanBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFuelAndCleanBinding.bind(view)

        binding.csbFuel.setOnSeekBarChangeListener(object :
            CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                fuelAndCleanViewModel.setFuel(progress)
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {}

            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {}
        })

        binding.btnNext.setOnClickListener {
            val fuel = fuelAndCleanViewModel.getFuel()
            val cleanState = fuelAndCleanViewModel.getCleanState()

            if (fuel != null && cleanState != null) {
                carActionViewModel.setFuelAndCleanState(fuel, cleanState)
            }
        }

        binding.rbtnClean.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                fuelAndCleanViewModel.setCleanState(CleanState.CLEAN)
                binding.btnNext.isEnabled = true
            }
        }

        binding.rbtnDirty.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                fuelAndCleanViewModel.setCleanState(CleanState.DIRTY)
                binding.btnNext.isEnabled = true
            }
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

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            fuelAndCleanViewModel.imageRes.collectLatest { imgRes ->
                imgRes?.let { scaleRes ->
                    // image from drawable
                    binding.imgFuelScale.setImageResource(scaleRes)
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