package anless.fleetmanagement.car_module.presentation.ui.car_details

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.ActionHistoryItem
import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.SimpleItem
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.ui.relocations.RelocationsViewModel
import anless.fleetmanagement.car_module.presentation.ui.simple_item.SimpleItemAdapter
import anless.fleetmanagement.car_module.presentation.utils.ActionManager
import anless.fleetmanagement.car_module.presentation.utils.Constants
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.ui.MainViewModel
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.core.app.presentation.utils.showToast
import anless.fleetmanagement.core.utils.DateFormatter
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentCarDetailsBinding
import anless.fleetmanagement.databinding.ItemHistoryActionBinding
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CarDetailsFragment : Fragment(R.layout.fragment_car_details),
    EasyPermissions.PermissionCallbacks {

    companion object {
        private const val TAG = "CarDetailsFragment"
    }

    private val carDetailsViewModel: CarDetailsViewModel by viewModels()
    private val relocationsViewModel: RelocationsViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!
    private val openInsuranceLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            carDetailsViewModel.clearInsuranceUri()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCarDetailsBinding.bind(view)

        binding.swipeRefresh.setOnRefreshListener {
            carDetailsViewModel.getCarInfo()
        }

        binding.layoutShowMore.setOnClickListener {
            binding.layoutShowMore.visibility = View.GONE
            binding.layoutExtraInfo.visibility = View.VISIBLE
        }

        binding.layoutHideInfo.setOnClickListener {
            binding.layoutExtraInfo.visibility = View.GONE
            binding.layoutShowMore.visibility = View.VISIBLE
        }

        binding.imgShowActionsHistory.setOnClickListener {
            setHistoryActionsVisibility(true)
        }

        binding.imgHideActionsHistory.setOnClickListener {
            setHistoryActionsVisibility(false)
        }

        binding.tvActionsHistoryTitle.setOnClickListener {
            if (binding.layoutHistoryActionItems.visibility == View.VISIBLE) {
                setHistoryActionsVisibility(false)
            } else {
                setHistoryActionsVisibility(true)
            }
        }

        binding.imgCleanState.setOnClickListener {
            val carStateRes = carDetailsViewModel.getStateCarTitle()

            carStateRes?.let { stateRes ->
                showToast(getString(R.string.car_state_info, getString(stateRes).lowercase()))
            }
        }

        val actionAdapter = SimpleItemAdapter { selectedAction ->
            carActionViewModel.setAction(selectedAction.id)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        with(binding.rvCarActions) {
            this.layoutManager = layoutManager
            this.adapter = actionAdapter
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.monitoring_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_monitoring -> {
                        findNavController().navigate(
                            CarDetailsFragmentDirections.actionCarDetailsFragmentToMonitoringFragment()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        (activity as MainActivity).setTitle(getString(R.string.car_actions))
        (activity as MainActivity).setSubTitle(null)

        subscribeUi(actionAdapter)
    }

    private fun subscribeUi(actionAdapter: SimpleItemAdapter) {
        mainViewModel.getShift()?.let { shift ->
            carActionViewModel.setIdStationOpenedShift(shift.station.id)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                } else {
                    //carDetailsViewModel.setIdCar(idCar)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carDetailsViewModel.isCommissioningNewCarAction.collectLatest { isAddNewCarAction ->
                if (isAddNewCarAction) {
                    carActionViewModel.setActionAddNewCar()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carDetailsViewModel.state.collectLatest { state ->
                when (state) {
                    is CarDetailsViewModel.CarDetailsState.Success -> {
                        setLoadingState(false)
                        state.resMessage?.let { message ->
                            showToast(getString(message))
                        }
                    }
                    is CarDetailsViewModel.CarDetailsState.Error -> {
                        setLoadingState(false)
                        val errorMessage = getString(state.resError)
                        showErrorDialog(errorMessage)

                        if (carDetailsViewModel.carIsEmpty()) {
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = errorMessage
                            binding.layoutCarDetails.visibility = View.GONE
                            binding.layoutActions.visibility = View.GONE
                            binding.layoutHistoryActions.visibility = View.GONE
                        }
                    }
                    is CarDetailsViewModel.CarDetailsState.Loading -> {
                        setLoadingState(true)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carDetailsViewModel.car.collectLatest { car ->
                if (car != null) {
                    showCarInfo(car)
                    carActionViewModel.setCar(car)
                    if (car.historyActions.isNotEmpty()) {
                        renderHistoryActions(car.historyActions)
                    } else {
                        binding.layoutHistoryActions.visibility = View.GONE
                    }
                } else {
                    // TODO: car is null action
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carDetailsViewModel.action.collectLatest { actions ->
                actionAdapter.submitList(actions.map { idAction ->
                    SimpleItem(idAction, getString(ActionManager.getTitleResString(idAction)))
                })

                binding.layoutActions.visibility =
                    if (actions.isEmpty()) View.GONE else View.VISIBLE
                //setActionsListVisibility()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            relocationsViewModel.relocations.collectLatest { relocations ->
                if (relocations != null) {
                    carActionViewModel.setRelocations(relocations)
                    //setActionsListVisibility()
                    binding.layoutActions.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            relocationsViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.state.collectLatest { state ->
                when (state) {
                    is CarActionViewModel.CarActionState.Empty -> {}
                    is CarActionViewModel.CarActionState.Loading -> {}
                    is CarActionViewModel.CarActionState.Success -> {
                    }
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carDetailsViewModel.insuranceUri.collectLatest { uri ->
                uri?.let { insuranceUri ->
                    openInsurance(insuranceUri)
                }
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setHistoryActionsVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.layoutHistoryActionItems.visibility = View.VISIBLE
            binding.imgShowActionsHistory.visibility = View.GONE
            binding.imgHideActionsHistory.visibility = View.VISIBLE
        } else {
            binding.layoutHistoryActionItems.visibility = View.GONE
            binding.imgShowActionsHistory.visibility = View.VISIBLE
            binding.imgHideActionsHistory.visibility = View.GONE
        }

        binding.scrollView.post {
            binding.scrollView.smoothScrollTo(0, binding.layoutHistoryActions.bottom)
            binding.scrollView.fling(binding.layoutHistoryActions.bottom)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        with(binding.swipeRefresh) {
            isRefreshing = isLoading
        }

        if (isLoading) {
            binding.layout.visibility = View.GONE
        } else {
            binding.layout.visibility = View.VISIBLE
        }
    }

    private fun showCarInfo(car: Car) {
        binding.layoutCarDetails.visibility = View.VISIBLE

        binding.tvCarModel.text = car.carInfo.model
        binding.tvCarLicensePlate.text = car.carInfo.licensePlate
        binding.tvCarCode.text = car.code
        binding.tvMileage.text = getString(R.string.kilometers, car.options.mileage)
        binding.tvLastTo.text = getString(R.string.on_km, car.options.maintenance.mileage)

        binding.tvOwner.text = car.owner
        val cleanStateColor = if (car.options.isClean) {
            MaterialColors.getColor(requireContext(), R.attr.icon_color, Color.GREEN)
        } else {
            MaterialColors.getColor(requireContext(), R.attr.text_primary_color, Color.GRAY)
        }
        binding.imgCleanState.imageTintList = ColorStateList.valueOf(cleanStateColor)

        carDetailsViewModel.getCurrentStatus()?.let { carStatusRes ->
            binding.tvCarStatus.text = getString(carStatusRes)
        }

        val hasInsurance = car.insurance != null

        binding.tvInsurance.isEnabled = hasInsurance
        var insuranceTextColor =
            MaterialColors.getColor(requireContext(), R.attr.text_primary_color, Color.DKGRAY)

        if (hasInsurance) {
            insuranceTextColor = if (carDetailsViewModel.isExpireInsurance()) {
                MaterialColors.getColor(
                    requireContext(),
                    com.google.android.material.R.attr.errorTextColor,
                    Color.DKGRAY
                )
            } else {
                MaterialColors.getColor(requireContext(), R.attr.icon_color, Color.GREEN)
            }
        }

        binding.tvInsurance.setTextColor(ColorStateList.valueOf(insuranceTextColor))

        binding.tvInsurance.text = if (hasInsurance) {
            getString(
                R.string.until_to,
                DateFormatter.formatDate(car.insurance!!.expireDate)
            )
        } else {
            getString(R.string.no)
        }

        binding.layoutInsurance.setOnClickListener {
            car.insurance?.let { insurance ->
                if (insurance.filename != null) {
                    downloadInsurance()
                } else {
                    showToast(getString(R.string.cant_find_insurance_file))
                }
            }
        }

        binding.layoutCarDetails.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    private fun openInsurance(uri: Uri) {
        val shareIntent = ShareCompat.IntentBuilder(requireContext())
            .setStream(uri)
            .setType(carDetailsViewModel.getInsuranceFileType())

        openInsuranceLauncher.launch(shareIntent.intent)
    }

    private fun checkClearAction() {
        if (carDetailsViewModel.isNewCar() && carActionViewModel.isActionClear()) {
            findNavController().popBackStack()
        }
    }

    private fun renderHistoryActions(historyItems: List<ActionHistoryItem>) {
        binding.layoutHistoryActionItems.removeAllViews()
        historyItems.forEach { item ->
            val historyActionBinding = ItemHistoryActionBinding.inflate(
                layoutInflater,
                binding.layoutHistoryActionItems,
                false
            )

            historyActionBinding.tvAction.text =
                getString(ActionManager.getTitleResString(item.status))
            historyActionBinding.tvDate.text = DateFormatter.formatFullDateTimeDeviceTimezone(item.createDate)
            historyActionBinding.tvManagerName.text = item.managerName
            historyActionBinding.tvStationCode.text = item.stationShortCode

            binding.layoutHistoryActionItems.addView(historyActionBinding.root)
            binding.layoutHistoryActions.visibility = View.VISIBLE
        }
    }


    private fun downloadInsurance() {
        if (!hasWriteStoragePermissions()) {
            requestPermissions()
            return
        }

        carDetailsViewModel.loadInsurance()
    }

    private fun hasWriteStoragePermissions() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )

    private fun requestPermissions() {
        if (hasWriteStoragePermissions()) {
            return
        }

        EasyPermissions.requestPermissions(
            this,
            getString(R.string.need_access_to_storage),
            Constants.REQUEST_CODE_WRITE_STORAGE_PERMISSION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        // Android >= Q ?

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        downloadInsurance()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause")
    }

    override fun onStart() {
        super.onStart()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
        carDetailsViewModel.setIdCar(carActionViewModel.getCarId())
        checkClearAction()
        relocationsViewModel.checkRelocations()
        carDetailsViewModel.getCarInfo()
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
        super.onDestroy()
    }
}