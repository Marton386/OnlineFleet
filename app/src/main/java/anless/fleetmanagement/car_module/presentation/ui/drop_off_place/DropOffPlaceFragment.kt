package anless.fleetmanagement.car_module.presentation.ui.drop_off_place

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.DropOffPlace
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentDropOffPlaceBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class DropOffPlaceFragment: Fragment(R.layout.fragment_drop_off_place) {

    companion object {
        const val TAG = "DropOffPlaceFragment"
    }

    private val dropOffPlaceViewModel: DropOffPlaceViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentDropOffPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDropOffPlaceBinding.bind(view)

        binding.rbtnOnStation.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                dropOffPlaceViewModel.setDropOffPlace(DropOffPlace.ON_STATION)
            }
        }

        binding.rbtnOnAddress.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                dropOffPlaceViewModel.setDropOffPlace(DropOffPlace.ON_ADDRESS)
            }
        }

        binding.btnNext.setOnClickListener {
            dropOffPlaceViewModel.getDropOffPlace()?.let { dropOffPlace ->
                var comment: String? = null
                if (dropOffPlace == DropOffPlace.ON_ADDRESS) {
                    comment = getString(R.string.drop_off_on_address)
                }
                carActionViewModel.setDropOffPlace(dropOffPlace, comment)
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
            dropOffPlaceViewModel.dropOffPlace.collectLatest { dropOffPlace ->
                binding.btnNext.isEnabled = (dropOffPlace != null)
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