package anless.fleetmanagement.car_module.presentation.ui.mileage

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.utils.showConfirmDialog
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentMileageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class MileageFragment : Fragment(R.layout.fragment_mileage) {

    companion object {
        const val TAG = "MileageFragment"
    }

    private val mileageViewModel: MileageViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentMileageBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMileageBinding.bind(view)

        AndroidUtils.openKeyboard(binding.etMileage)

        binding.etMileage.addTextChangedListener { mileage ->
            if (binding.layoutMileage.error != null) {
                binding.layoutMileage.error = null
            }

            val mileageText = mileage.toString().trim()
            if (mileageText.isNotEmpty()) {
                mileageViewModel.setMileage(mileageText)
            }
        }

        binding.btnNext.setOnClickListener {
            if (mileageViewModel.isMileageCorrect()) {
                if (mileageViewModel.getMileage().toString() != carActionViewModel.reservationNumber) {
                    binding.layoutMileage.error = null
                    mileageViewModel.getMileage()?.let { mileage ->
                        carActionViewModel.setMileage(mileage)
                    }
                } else {
                    val confirmDialog = showConfirmDialog(getString(R.string.mileage_confirm))
                    confirmDialog?.let { dialog ->
                        dialog.findViewById<Button>(R.id.btn_ok)?.setText(R.string.yes)
                        dialog.findViewById<Button>(R.id.btn_cancel)?.setText(R.string.no)
                        dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
                            binding.layoutMileage.error = null
                            mileageViewModel.getMileage()?.let { mileage ->
                                carActionViewModel.setMileage(mileage)
                            }
                            dialog.dismiss()
                        }
                    }
                }
            } else {
                binding.layoutMileage.error =
                    getString(R.string.incorrect_milage_contact_regional_manager)
            }
        }

        carActionViewModel.getCar()?.let { car ->
            mileageViewModel.setCar(car)
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
            mileageViewModel.mileage.collectLatest { mileage ->
                binding.btnNext.isEnabled = mileage != null

                if (mileage == null) {
                    binding.layoutMileage.error = null
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