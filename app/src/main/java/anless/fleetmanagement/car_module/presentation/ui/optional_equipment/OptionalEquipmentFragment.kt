package anless.fleetmanagement.car_module.presentation.ui.optional_equipment

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
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentOptionalEquipmentBinding
import anless.fleetmanagement.databinding.ItemOptionalEquipmentBinding
import anless.fleetmanagement.core.app.presentation.utils.showConfirmDialog
import anless.fleetmanagement.core.app.presentation.utils.showMessageDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class OptionalEquipmentFragment : Fragment(R.layout.fragment_optional_equipment) {

    companion object {
        const val TAG = "FragmentOptionalEquipment"
    }

    private val optionalEquipmentViewModel: OptionalEquipmentViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentOptionalEquipmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOptionalEquipmentBinding.bind(view)

        bindProgressButton(binding.btnAddEquipment)

        binding.rbtnGps.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                binding.layoutAddEquipment.visibility = View.VISIBLE
                binding.layoutNumberEquipment.visibility = View.GONE
                binding.btnAddEquipment.isEnabled = true
                optionalEquipmentViewModel.selectGpsNavigator()
            }
        }

        binding.rbtnChildSeat.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                binding.layoutAddEquipment.visibility = View.VISIBLE
                binding.layoutNumberEquipment.visibility = View.VISIBLE
                binding.btnAddEquipment.isEnabled = false
                optionalEquipmentViewModel.selectChildSeat()
            }
        }

        binding.etNumberEquipment.addTextChangedListener { text ->
            optionalEquipmentViewModel.setEquipmentCode(text.toString().trim())
        }

        binding.btnAddEquipment.setOnClickListener {
            optionalEquipmentViewModel.addEquipment()
        }

        binding.btnNext.setOnClickListener {
            //carActionViewModel.setOptionalEquipment(optionalEquipmentViewModel.getSelectedEquipment())
        }

        val resTitle = carActionViewModel.getActionTitle()
        resTitle?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        val car = carActionViewModel.getCar()
        car?.carInfo?.licensePlate?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi()
    }

    private fun subscribeUi() {
        lifecycleScope.launchWhenStarted {
            optionalEquipmentViewModel.state.collectLatest { state ->
                when (state) {
                    is OptionalEquipmentViewModel.OptionalEquipmentState.Empty -> {}
                    is OptionalEquipmentViewModel.OptionalEquipmentState.Loading -> {
                        setLoadingState(true)
                    }
                    is OptionalEquipmentViewModel.OptionalEquipmentState.Success -> {
                        setLoadingState(false)
                    }
                    is OptionalEquipmentViewModel.OptionalEquipmentState.Error -> {
                        showMessageDialog(getString(state.resError))
                        setLoadingState(false)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            optionalEquipmentViewModel.equipmentCode.collectLatest { equipmentCode ->
                binding.btnAddEquipment.isEnabled = equipmentCode.isNotEmpty()

                if (equipmentCode.isEmpty() && binding.etNumberEquipment.text.toString()
                        .isNotEmpty()
                ) {
                    binding.etNumberEquipment.setText("")
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            optionalEquipmentViewModel.selectedEquipments.collectLatest { equipmentList ->
                renderOptionalEquipment(equipmentList)

                binding.tvEquipmentNotSelected.visibility =
                    if (equipmentList.isEmpty()) View.VISIBLE else View.GONE

                binding.layoutOptionalEquipment.visibility =
                    if (equipmentList.isEmpty()) View.INVISIBLE else View.VISIBLE
            }
        }

        lifecycleScope.launchWhenStarted {
            optionalEquipmentViewModel.equipmentType.collectLatest { equipmentType ->
                if (equipmentType == null) {
                    binding.rbtnGps.isChecked = false
                    binding.rbtnChildSeat.isChecked = false
                    binding.layoutAddEquipment.visibility = View.GONE
                }
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnAddEquipment.showProgress()
        } else {
            binding.btnAddEquipment.hideProgress(R.string.add_equipment)
        }

        binding.btnAddEquipment.isEnabled = !isLoading
        binding.btnNext.isEnabled = !isLoading
    }

    private fun renderOptionalEquipment(equipmentList: List<Equipment>) {
        binding.layoutSelectedEquipment.removeAllViews()
        equipmentList.forEach { item ->
            val itemOptionalEquipmentBinding = ItemOptionalEquipmentBinding.inflate(
                layoutInflater,
                binding.layoutSelectedEquipment,
                false
            )

            var title = item.description

            if (title.isEmpty()) {
                title = getString(optionalEquipmentViewModel.getEquipmentTitleRes(item.type))
            }

            itemOptionalEquipmentBinding.tvTitle.text = title
            itemOptionalEquipmentBinding.tvCode.text = item.code

            binding.layoutSelectedEquipment.addView(itemOptionalEquipmentBinding.root)
            itemOptionalEquipmentBinding.root.setOnClickListener {
                val confirmDialog =
                    showConfirmDialog(getString(R.string.do_you_want_delete_str, title))
                confirmDialog?.let { dialog ->
                    dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
                        optionalEquipmentViewModel.deleteEquipmentFromList(item)
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}