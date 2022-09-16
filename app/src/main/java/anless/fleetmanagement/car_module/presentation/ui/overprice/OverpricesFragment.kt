package anless.fleetmanagement.car_module.presentation.ui.overprice

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentOverpricesBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class OverpricesFragment : Fragment(R.layout.fragment_overprices) {

    companion object {
        const val TAG = "OverpricesFragment"
    }

    private val overpriceViewModel: OverpricesViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentOverpricesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOverpricesBinding.bind(view)

        bindProgressButton(binding.btnNext)

        binding.btnNext.setOnClickListener {
            carActionViewModel.setOverpricesChecked()
        }

        binding.imgRefresh.setOnClickListener {
            overpriceViewModel.checkOverprices()
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
                } else {
                    carActionViewModel.getCarInfo()?.let { carParam ->
                        overpriceViewModel.setCarInfo(idCar, carParam)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            overpriceViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            overpriceViewModel.errorRes.collectLatest { errorRes ->
                errorRes?.let { error ->
                    val errorMessage = getString(error)
                    showErrorDialog(errorMessage)
                    binding.tvCheckingExcesses.text = errorMessage
                    binding.tvCheckingExcesses.visibility = View.VISIBLE
                    binding.imgRefresh.visibility = View.VISIBLE
                    binding.layoutOverprices.visibility = View.GONE
                    binding.btnNext.isEnabled = false
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            overpriceViewModel.overprices.collectLatest { overprices ->
                overprices?.let { excesses ->
                    setOverprices(excesses)
                    binding.imgRefresh.visibility = View.GONE
                    binding.tvCheckingExcesses.visibility = View.GONE
                    binding.layoutOverprices.visibility = View.VISIBLE
                    binding.btnNext.isEnabled = true
                }
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnNext.showProgress()
            binding.btnNext.isEnabled = false
            binding.imgRefresh.visibility = View.GONE
            binding.layoutOverprices.visibility = View.GONE
            binding.tvCheckingExcesses.visibility = View.VISIBLE
            binding.tvCheckingExcesses.text = getString(R.string.checking_for_excesses)
        } else {
            binding.btnNext.hideProgress(R.string.next)
        }
    }

    private fun setOverprices(overprices: Overprices) {
        val emptyPay = 0
        var needTakePay = false

        val needPayTextColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.errorTextColor,
            Color.RED
        )
        val defaultTextColor =
            MaterialColors.getColor(requireContext(), R.attr.text_primary_color, Color.LTGRAY)


        if (overprices.mileageExcess > emptyPay) {
            needTakePay = true
            binding.tvMileageExcess.text = overprices.mileageExcess.toString()
            binding.tvMileageExcess.setTextColor(needPayTextColor)
        } else {
            binding.tvMileageExcess.text = getString(R.string.no)
            binding.tvMileageExcess.setTextColor(defaultTextColor)
        }

        if (overprices.carWashPay) {
            needTakePay = true
            binding.tvNeedCarwash.text = getString(R.string.yes)
            binding.tvNeedCarwash.setTextColor(needPayTextColor)
        } else {
            binding.tvNeedCarwash.text = getString(R.string.no)
            binding.tvNeedCarwash.setTextColor(defaultTextColor)
        }

        if (overprices.fuelPay) {
            needTakePay = true
            binding.tvFuelPay.text = getString(R.string.yes)
            binding.tvFuelPay.setTextColor(needPayTextColor)
        } else {
            binding.tvFuelPay.text = getString(R.string.no)
            binding.tvFuelPay.setTextColor(defaultTextColor)
        }

        if (overprices.fineCost > emptyPay) {
            needTakePay = true
            binding.tvCostFines.text = overprices.fineCost.toString()
            binding.tvCostFines.setTextColor(needPayTextColor)
        } else {
            binding.tvCostFines.text = getString(R.string.no)
            binding.tvCostFines.setTextColor(defaultTextColor)
        }

        binding.tvRememberTakePay.visibility = if (needTakePay) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}