package anless.fleetmanagement.car_module.presentation.ui.qr

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.ui.reservation.ReservationViewModel
import anless.fleetmanagement.car_module.presentation.utils.ScanBarcodeContract
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.ui.MainViewModel
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.core.app.presentation.utils.showToast
import anless.fleetmanagement.databinding.FragmentQrCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class QrFragment : Fragment(R.layout.fragment_qr_code), EasyPermissions.PermissionCallbacks {
    companion object {
        const val TAG = "SearchCarFragment"
        const val VEHICLE_TAG = "VEH"
        const val HTTP_TAG = "https://rentmotors.ru/offer/"
    }

    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val reservationViewModel: ReservationViewModel by activityViewModels()
    private val qrViewModel: QrViewModel by activityViewModels()
    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    private val scanBarcodeLauncher =
        registerForActivityResult(ScanBarcodeContract(R.string.scan_car_qr_code)) { barcode ->
            if (barcode.isEmpty()) {
                return@registerForActivityResult
            }
            if (barcode.startsWith(VEHICLE_TAG)) {
                val idCar = barcode.substring(VEHICLE_TAG.length).toInt()
                selectIdCar(idCar)
            } else {
                if (barcode.startsWith(HTTP_TAG)) {
                    var startIndex = 0
                    var endIndex = 0
                    barcode.forEachIndexed { index, char ->
                        if (char == '=' && startIndex == 0) startIndex = index + 1
                    }
                    barcode.forEachIndexed { index, char -> if (char == '&') endIndex = index }
                    val idContract = barcode.substring(startIndex, endIndex)
                    selectIdContract(idContract)
                } else
                    showToast(getString(R.string.cant_recognize_qr_code))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQrCodeBinding.bind(view)
        binding.progressBar.visibility = View.GONE
        qrViewModel.clearAll()
        scanBarcode()
        binding.scanQrcode.setOnClickListener {
            qrViewModel.clearAll()
            scanBarcode()
        }
        (activity as MainActivity).setTitle(getString(R.string.qr_code_scan))
        (activity as MainActivity).setSubTitle(null)
        subscribeUi()
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            qrViewModel.contract.collectLatest { contract ->
                if (contract != null) {
                    if (contract.status.toInt() != 0) {
                        mainViewModel.getShift()?.let { shift ->
                            carActionViewModel.setIdStationOpenedShift(shift.station.id)
                        }
                        carActionViewModel.setCarId(contract.vehicleID.toInt())
                        reservationViewModel.setIdCar(contract.vehicleID.toInt())
                        reservationViewModel.setResNumber(contract.refid)
                        if (contract.status.toInt() != 2)
                            reservationViewModel.searchReservations()
                        else {
                            carActionViewModel.setScreensList(2)
                            (activity as MainActivity).openSureDrop()
                            endLoadData()
                        }
                    } else {
                        showErrorDialog(getString(R.string.close_contract))
                        endLoadData()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.reservations.collectLatest { reservations ->
                if (reservations.isEmpty()) {
                    showErrorDialog(getString(R.string.scan_error))
                    endLoadData()
                } else {
                    reservationViewModel.setReservation(reservations[0])
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.reservation.collect { reservation ->
                carActionViewModel.setReservation(reservation)
                if (qrViewModel.contract.value!!.status.toInt() == 1 || qrViewModel.contract.value!!.status.toInt() == 3) {
                    carActionViewModel.setScreensList(-1)
                    (activity as MainActivity).openSureDrop()
                } else {
                    showErrorDialog(getString(R.string.close_contract))
                }
                endLoadData()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            reservationViewModel.errorRes.collectLatest { errorRes ->
                endLoadData()
                showErrorDialog(getString(errorRes))
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            qrViewModel.errorRes.collectLatest { errorRes ->
                endLoadData()
                showErrorDialog(getString(errorRes))
            }
        }
    }

    private fun selectIdCar(idCar: Int) {
        carActionViewModel.setCarId(idCar)
        (activity as MainActivity).openCarDetails()
    }

    private fun selectIdContract(idContract: String) {
        startLoadData()
        qrViewModel.searchContract(idContract)
    }

    private fun startLoadData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.scanQrcode.visibility = View.GONE
        binding.textView.visibility = View.GONE
    }

    private fun endLoadData() {
        binding.progressBar.visibility = View.GONE
        binding.scanQrcode.visibility = View.VISIBLE
        binding.textView.visibility = View.VISIBLE
    }

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
    }

    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CAMERA
        )

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
        scanBarcodeLauncher.launch()
    }
}