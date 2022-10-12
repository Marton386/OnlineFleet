package anless.fleetmanagement.car_module.presentation.ui.send_action

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.ActionManager
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentSendActionBinding
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SendActionFragment : Fragment(R.layout.fragment_send_action) {

    companion object {
        const val TAG = "SendActionFragment"
    }

    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentSendActionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            if (carActionViewModel.isActionSent()) {
                clearScreens()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSendActionBinding.bind(view)

        bindProgressButton(binding.btnDone)

        binding.btnDone.setOnClickListener {
            clearScreens()
        }

        binding.imgRefresh.setOnClickListener {
            carActionViewModel.sendAction()
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
                    val action = SendActionFragmentDirections.actionSendActionFragmentToSearchCarFragment()
                    findNavController().navigate(action)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.state.collectLatest { state ->
                when (state) {
                    is CarActionViewModel.CarActionState.Loading -> {
                        setLoadingState(true)
                    }
                    is CarActionViewModel.CarActionState.Success -> {
                        setLoadingState(false)
                        binding.tvSendingInfo.text = getString(state.messageRes)
                        binding.imgRefresh.visibility = View.GONE
                        binding.btnDone.isEnabled = true
                    }
                    is CarActionViewModel.CarActionState.Error -> {
                        setLoadingState(false)
                        var errorMessage = getString(state.errorRes)
                        if (state.errorRes == R.string.not_enough_parameters) {
                            errorMessage += "\nНедостающие параметры:"
                            if (carActionViewModel.getAction() == null)
                                errorMessage += " действие == null;"
                            if (carActionViewModel.getIdCar() == null)
                                errorMessage += " id авто == null;"
                            if (carActionViewModel.getIdStationOpenedShift() == null)
                                errorMessage += " id станции == null;"
                            if (carActionViewModel.getMileage() == null)
                                errorMessage += " пробег == null;"
                            if (carActionViewModel.getFuel() == null)
                                errorMessage += " топливо == null;"
                            if (carActionViewModel.getCleanState() == null)
                                errorMessage += " чистота == null;"
                            if (carActionViewModel.getFilenamePhotoAct() == null)
                                errorMessage += " имя фото равно == null;"
                        }
                        binding.tvSendingInfo.text = errorMessage
                        binding.imgRefresh.visibility = View.VISIBLE
                        binding.btnDone.isEnabled = false
                        carActionViewModel.clearState()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.extraAction.collectLatest { extraAction ->
                setButtonExtraAction(extraAction)
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnDone.showProgress()
            binding.tvSendingInfo.text = getString(R.string.sending_data)
            binding.imgRefresh.visibility = View.GONE
        } else {
            binding.btnDone.hideProgress(R.string.done)
        }

        binding.btnDone.isEnabled = !isLoading
        binding.btnExtraAction.isEnabled = !isLoading
    }

    private fun setButtonExtraAction(extraAction: Int?) {
        if (extraAction != null) {
            binding.btnExtraAction.text = getString(
                ActionManager.getTitleResString(extraAction)
            )
            binding.btnExtraAction.visibility = View.VISIBLE

            binding.btnExtraAction.setOnClickListener {
                carActionViewModel.selectExtraAction()
            }
        } else {
            binding.btnExtraAction.visibility = View.GONE
            binding.btnExtraAction.text = ""
        }
    }

    private fun clearScreens() {
        carActionViewModel.clearAll()
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)

        carActionViewModel.getActionTitle()?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }
        //setLoadingState(carActionViewModel.isExtraActionAvailable())
    }
}