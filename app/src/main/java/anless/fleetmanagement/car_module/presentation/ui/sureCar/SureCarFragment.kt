package anless.fleetmanagement.car_module.presentation.ui.sureCar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.ui.car_details.CarDetailsViewModel
import anless.fleetmanagement.car_module.presentation.ui.qr.QrViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.databinding.FragmentSureDropExtradCarBinding
import kotlinx.coroutines.flow.collectLatest

class SureCarFragment : Fragment(R.layout.fragment_sure_drop_extrad_car) {
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private val carDetailsViewModel: CarDetailsViewModel by activityViewModels()
    private val qrViewModel: QrViewModel by activityViewModels()
    private var _binding: FragmentSureDropExtradCarBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSureDropExtradCarBinding.bind(view)
        startLoadData()
        (activity as MainActivity).setTitle(getString(R.string.pick_up))
        (activity as MainActivity).setSubTitle(null)
        subscribeUi()
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            qrViewModel.contract.collectLatest { contract ->
                if (contract != null) {
                    binding.tvClient.text = contract.client.fullTitle
                    binding.tvAgreement.text = qrViewModel.contractId.toString()

                    if (contract.status.toInt() != 2) {
                        (activity as MainActivity).setTitle(getString(R.string.pick_up))
                        (activity as MainActivity).setSubTitle(null)
                        binding.tvTitle.text = getString(R.string.pick_up_car)
                        binding.btnOk.setOnClickListener {
                            (activity as MainActivity).openMileage()
                        }
                    }
                    else {
                        (activity as MainActivity).setTitle(getString(R.string.drop_off))
                        (activity as MainActivity).setSubTitle(null)
                        binding.tvTitle.text = getString(R.string.drop_off_car)
                        binding.btnOk.setOnClickListener {
                            (activity as MainActivity).openDropOffPlace()
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                        carDetailsViewModel.car.collectLatest { car ->
                            if (car != null) {
                                if (car.status == 2) {
                                    carActionViewModel.setCar(car)
                                    binding.tvCarModel.text = car.carInfo.model
                                    binding.tvCarLicensePlate.text = car.carInfo.licensePlate
                                    endLoadData()
                                }
                                else {
                                    if (car.status == 1 && contract.status.toInt() == 2) {
                                        carActionViewModel.setCar(car)
                                        binding.tvCarModel.text = car.carInfo.model
                                        binding.tvCarLicensePlate.text = car.carInfo.licensePlate
                                        endLoadData()
                                    }
                                    else {
                                        (activity as MainActivity).onBackPressed()
                                        val alertDialog = showErrorDialog(getString(R.string.error_car))
                                        alertDialog?.let { dialog ->
                                            dialog.setOnDismissListener {
                                                carActionViewModel.clearState()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    (activity as MainActivity).onBackPressed()
                    val alertDialog = showErrorDialog(getString(R.string.error))
                    alertDialog?.let { dialog ->
                        dialog.setOnDismissListener {
                            carActionViewModel.clearState()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.state.collectLatest { state ->
                when (state) {
                    is CarActionViewModel.CarActionState.Empty -> {}
                    is CarActionViewModel.CarActionState.Loading -> {}
                    is CarActionViewModel.CarActionState.Success -> {}
                    is CarActionViewModel.CarActionState.Error -> {
                        val alertDialog = showErrorDialog(getString(state.errorRes))
                        alertDialog?.let { dialog ->
                            dialog.setOnDismissListener {
                                carActionViewModel.clearState()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startLoadData() {
        binding.tvTitle.visibility = View.GONE
        binding.tvTitleModel.visibility = View.GONE
        binding.tvCarModel.visibility = View.GONE
        binding.tvTitleNumber.visibility = View.GONE
        binding.tvCarLicensePlate.visibility = View.GONE
        binding.tvTitleClient.visibility = View.GONE
        binding.tvClient.visibility = View.GONE
        binding.tvTitleAgreement.visibility = View.GONE
        binding.tvAgreement.visibility = View.GONE
        binding.tvWarning.visibility = View.GONE
        binding.btnOk.visibility = View.GONE

        binding.progressBar2.visibility = View.VISIBLE
    }

    private fun endLoadData() {
        binding.tvTitle.visibility = View.VISIBLE
        binding.tvTitleModel.visibility = View.VISIBLE
        binding.tvCarModel.visibility = View.VISIBLE
        binding.tvTitleNumber.visibility = View.VISIBLE
        binding.tvCarLicensePlate.visibility = View.VISIBLE
        binding.tvTitleClient.visibility = View.VISIBLE
        binding.tvClient.visibility = View.VISIBLE
        binding.tvTitleAgreement.visibility = View.VISIBLE
        binding.tvAgreement.visibility = View.VISIBLE
        binding.tvWarning.visibility = View.VISIBLE
        binding.btnOk.visibility = View.VISIBLE

        binding.progressBar2.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        carDetailsViewModel.clearCar()
        carDetailsViewModel.setIdCar(carActionViewModel.getCarId())
        carDetailsViewModel.getCarWithoutActions()
    }
}