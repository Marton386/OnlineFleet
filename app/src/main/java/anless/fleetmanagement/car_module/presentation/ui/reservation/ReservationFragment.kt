package anless.fleetmanagement.car_module.presentation.ui.reservation

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.launch
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.ReservationItem
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.ScanBarcodeContract
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentReservationBinding
import anless.fleetmanagement.databinding.ItemReservationBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.core.app.presentation.utils.showToast
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class ReservationFragment : Fragment(R.layout.fragment_reservation),
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val TAG = "ReservationFragment"
        const val RESERVATION_TAG = "AGR"
    }

    private val reservationViewModel: ReservationViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private val scanReservationNumberLauncher =
        registerForActivityResult(ScanBarcodeContract(R.string.scan_qr_code_res_number)) { barcode ->
            if (barcode.isEmpty()) {
                return@registerForActivityResult
            }

            if (barcode.startsWith(RESERVATION_TAG)) {
                val resNumber = barcode.substring(RESERVATION_TAG.length)
                binding.etReservationNumber.setText(resNumber)
                reservationViewModel.setResNumber(resNumber)
                reservationViewModel.searchReservations()
            } else {
                showToast(getString(R.string.cant_recognize_contract_number))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReservationBinding.bind(view)

        bindProgressButton(binding.btnNext)

        AndroidUtils.openKeyboard(binding.etReservationNumber)

        binding.etReservationNumber.addTextChangedListener { reservation ->
            val resNumber = reservation.toString().trim()
            if (resNumber.isEmpty() || resNumber != reservationViewModel.getResNumber()) {
                reservationViewModel.clearReservations()
            }
            reservationViewModel.setResNumber(resNumber)
            binding.tvResNoFound.visibility = View.GONE
            binding.layoutScanQrcode.visibility =
                if (resNumber.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.btnNext.setOnClickListener {
            reservationViewModel.searchReservations()
        }

        binding.layoutScanQrcode.setOnClickListener {
            scanBarcode()
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
                    reservationViewModel.setIdCar(idCar)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.reservationNumber.collectLatest { resNumber ->
                binding.btnNext.isEnabled = !resNumber.isNullOrBlank()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.reservations.collectLatest { reservations ->
                if (reservations.isEmpty()) {
                    setReservationsFound(false)
                } else {
                    renderHistoryActions(reservations)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.errorRes.collectLatest { errorRes ->
                showErrorDialog(getString(errorRes))
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.reservation.collect { reservation ->
                carActionViewModel.setReservation(reservation)
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setReservationsFound(isFound: Boolean) {
        if (isFound) {
            binding.layoutReservations.visibility = View.VISIBLE
            binding.tvResNoFound.visibility = View.GONE
            binding.btnNext.visibility = View.GONE
        } else {
            binding.layoutReservations.visibility = View.INVISIBLE
            binding.tvResNoFound.visibility = View.VISIBLE
            binding.btnNext.visibility = View.VISIBLE
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnNext.showProgress()
        } else {
            binding.btnNext.hideProgress(R.string.next)
        }
        binding.btnNext.isEnabled =
            !isLoading && binding.etReservationNumber.text.toString().isNotEmpty()
    }

    private fun renderHistoryActions(reservationItems: List<ReservationItem>) {
        binding.layoutReservationItems.removeAllViews()
        reservationItems.forEach { item ->
            val reservationBinding = ItemReservationBinding.inflate(
                layoutInflater,
                binding.layoutReservationItems,
                false
            )

            reservationBinding.tvClient.text = item.clientName
            reservationBinding.tvResNumber.text = item.resNumber
            reservationBinding.tvSourceName.text = item.source.name
            reservationBinding.tvCompanyCode.text = item.company

            var errorVisibility = View.GONE
            if (item.error != null) {
                reservationViewModel.getErrorByCode(item.error)?.let { errorRes ->
                    reservationBinding.tvError.text =
                        getString(errorRes)
                    errorVisibility = View.VISIBLE
                }
            }
            reservationBinding.tvError.visibility = errorVisibility
            binding.layoutReservationItems.addView(reservationBinding.root)
            reservationBinding.root.setOnClickListener {
                reservationViewModel.setReservation(item)
            }
        }
        setReservationsFound(true)
    }


    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CAMERA
        )

    private fun requestPermissions() {
        if (hasCameraPermission()) {
            return
        }

        EasyPermissions.requestPermissions(
            this,
            getString(R.string.need_camera_access),
            anless.fleetmanagement.user_module.presentation.utils.Constants.REQUEST_CODE_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )
        // Android >= Q ?

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        scanBarcode()
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

    private fun scanBarcode() {
        if (!hasCameraPermission()) {
            requestPermissions()
            return
        }

        scanReservationNumberLauncher.launch()
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}