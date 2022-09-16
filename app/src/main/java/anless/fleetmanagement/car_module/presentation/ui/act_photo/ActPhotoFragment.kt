package anless.fleetmanagement.car_module.presentation.ui.act_photo

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentActPhotoBinding
import anless.fleetmanagement.user_module.presentation.utils.Constants
import anless.fleetmanagement.core.app.presentation.utils.showMessageDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class ActPhotoFragment : Fragment(R.layout.fragment_act_photo),
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val TAG = "ActPhotoFragment"
    }

    private val actPhotoViewModel: ActPhotoViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentActPhotoBinding? = null
    private val binding get() = _binding!!

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                val uri = actPhotoViewModel.getPhotoActUri()
                if (uri != null) {
                    actPhotoViewModel.setPhotoAct(uri)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentActPhotoBinding.bind(view)

        bindProgressButton(binding.btnNext)

        binding.layoutOpenCamera.setOnClickListener {
            takePhoto()
        }

        binding.btnNext.setOnClickListener {
            actPhotoViewModel.sendPhotoAct()
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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            actPhotoViewModel.photoAct.collectLatest { photo ->
                binding.btnNext.isEnabled = photo != null
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            actPhotoViewModel.state.collectLatest { state ->
                when (state) {
                    is ActPhotoViewModel.ActPhotoState.Empty -> {}
                    is ActPhotoViewModel.ActPhotoState.Loading -> {
                        setLoadingState(true)
                    }
                    is ActPhotoViewModel.ActPhotoState.Success -> {
                        setLoadingState(false)
                        carActionViewModel.setFilenamePhotoAct(state.savedPhotoFilename)
                    }
                    is ActPhotoViewModel.ActPhotoState.Error -> {
                        showMessageDialog(getString(state.resError))
                        setLoadingState(false)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            actPhotoViewModel.photoActThumbnail.collectLatest { photoThumbnail ->
                if (photoThumbnail != null) {
                    binding.imgPhotoAct.setImageBitmap(photoThumbnail)
                    binding.layoutLoadedPhoto.visibility = View.VISIBLE
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
        } else {
            binding.btnNext.hideProgress(R.string.next)
        }

        binding.btnNext.isEnabled = !isLoading
        binding.layoutOpenCamera.isEnabled = !isLoading
    }

    private fun hasCameraPermission() = actPhotoViewModel.hasCameraPermissions()

    private fun requestPermissions() {
        if (hasCameraPermission()) {
            return
        }

        EasyPermissions.requestPermissions(
            this,
            getString(R.string.need_camera_access),
            Constants.REQUEST_CODE_CAMERA_PERMISSION,
            Manifest.permission.CAMERA
        )
        // Android >= Q ?

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        takePhoto()
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

    private fun takePhoto() {
        if (!hasCameraPermission()) {
            requestPermissions()
            return
        }

        val uri = actPhotoViewModel.createImageFileUri()

        if (uri != null) {
            actPhotoViewModel.setPhotoActUri(uri)
            takePictureLauncher.launch(uri)
        }
    }
}