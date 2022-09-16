package anless.fleetmanagement.car_module.presentation.ui.tire_options

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.TireParams
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.TireOptions
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentTireOptionsBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TireOptionsFragment : Fragment(R.layout.fragment_tire_options) {

    companion object {
        private const val TAG = "TireOptionsFragment"
    }

    private val tireOptionsViewModel: TireOptionsViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentTireOptionsBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTireOptionsBinding.bind(view)

        binding.btnNext.setOnClickListener {
            carActionViewModel.setTireParams(tireOptionsViewModel.getTireParams())
            //navigateNextScreen()
            // TODO: block button for only one click in time
        }

        setWidthValues()
        binding.numpWidth.setOnValueChangedListener { picker, oldVal, newVal ->
            tireOptionsViewModel.setWidth((newVal - 1))
        }

        setProfileValues()
        binding.numpProfile.setOnValueChangedListener { picker, oldVal, newVal ->
            tireOptionsViewModel.setProfile((newVal - 1))
        }

        setDiameterValues()
        binding.numpDiameter.setOnValueChangedListener { picker, oldVal, newVal ->
            tireOptionsViewModel.setDiameter(newVal)
        }

        binding.rbtnSummerTires.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireType(TireParams.Type.SUMMER)
            }
        }

        binding.rbtnWinterStuddedTires.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireType(TireParams.Type.WINTER_STUDDED)
            }
        }

        binding.rbtnWinterStudlessTires.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireType(TireParams.Type.WINTER_STUDLESS)
            }
        }

        binding.rbtnNew.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireCondition(TireParams.Condition.NEW)
            }
        }

        binding.rbtnGood.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireCondition(TireParams.Condition.GOOD)
            }
        }

        binding.rbtnOneSeason.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                tireOptionsViewModel.setTireCondition(TireParams.Condition.FOR_ONE_SEASON)
            }
        }

        binding.imgRefresh.setOnClickListener {
            tireOptionsViewModel.getCurrentTireType()
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
                } else {
                    tireOptionsViewModel.setIdCar(idCar)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tireOptionsViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tireOptionsViewModel.currentTireType.collectLatest { tireType ->
                tireType?.let {
                    when (tireType) {
                        TireParams.Type.SUMMER -> {
                            binding.rbtnSummerTires.isEnabled = false
                            binding.rbtnWinterStuddedTires.isEnabled = true
                            binding.rbtnWinterStudlessTires.isEnabled = true
                        }
                        TireParams.Type.WINTER_STUDDED -> {
                            binding.rbtnSummerTires.isEnabled = true
                            binding.rbtnWinterStuddedTires.isEnabled = false
                            binding.rbtnWinterStudlessTires.isEnabled = true
                        }
                        TireParams.Type.WINTER_STUDLESS -> {
                            binding.rbtnSummerTires.isEnabled = true
                            binding.rbtnWinterStuddedTires.isEnabled = true
                            binding.rbtnWinterStudlessTires.isEnabled = false
                        }
                    }
                    binding.layout.visibility = View.VISIBLE
                    binding.layoutRefresh.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tireOptionsViewModel.errorMessage.collectLatest { errorRes ->
                errorRes?.let { error ->
                    val errorMessage = getString(error)
                    showErrorDialog(errorMessage)
                    binding.tvCheckingInfo.text = errorMessage
                    binding.tvCheckingInfo.visibility = View.VISIBLE
                    binding.imgRefresh.visibility = View.VISIBLE
                    binding.layout.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            tireOptionsViewModel.tireParamsReady.collectLatest { state ->
                binding.btnNext.isEnabled = state
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layout.visibility = View.INVISIBLE
            binding.tvCheckingInfo.text = getString(R.string.checking_instaled_tires)
            binding.imgRefresh.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.tvCheckingInfo.text = ""
            //binding.layout.visibility = View.VISIBLE
        }
    }

    private fun setWidthValues() {
        val widthValues = tireOptionsViewModel.getWidthValues()
        binding.numpWidth.minValue = 1
        binding.numpWidth.maxValue = widthValues.size
        binding.numpWidth.displayedValues = widthValues
    }

    private fun setProfileValues() {
        val profileValues = tireOptionsViewModel.getProfileValues()
        binding.numpProfile.minValue = 1
        binding.numpProfile.maxValue = profileValues.size
        binding.numpProfile.displayedValues = profileValues
    }

    private fun setDiameterValues() {
        binding.numpDiameter.minValue = TireOptions.Diameter.MIN_VALUE
        binding.numpDiameter.maxValue = TireOptions.Diameter.MAX_VALUE
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}