package anless.fleetmanagement.car_module.presentation.ui.maitenance

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.RepairType
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentMaintenanceBinding
import anless.fleetmanagement.databinding.ItemMaintenanceBinding
import anless.fleetmanagement.core.app.presentation.utils.showConfirmDialog
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MaintenanceFragment : Fragment(R.layout.fragment_maintenance),
    EasyPermissions.PermissionCallbacks {

    companion object {
        const val TAG = "MaintenanceFragment"
    }

    private val maintenanceViewModel: MaintenanceViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentMaintenanceBinding? = null
    private val binding get() = _binding!!

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                val uri = maintenanceViewModel.getPhotoUri()
                if (uri != null) {
                    maintenanceViewModel.addPhotos(listOf(uri))
                }
            }
        }


    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            if (uriList.isNotEmpty()) {
                maintenanceViewModel.addPhotos(uriList)
            }
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMaintenanceBinding.bind(view)

        bindProgressButton(binding.btnNext)

        val adapterPhotos = PhotoAdapter { selectedPhotoItem ->
            if (binding.rbtnMaintenance.isEnabled) {
                val confirmDialog =
                    showConfirmDialog(getString(R.string.delete_this_photo))
                confirmDialog?.let { dialog ->
                    dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
                        maintenanceViewModel.deletePhotoFromList(selectedPhotoItem)
                        dialog.dismiss()
                    }
                }
            }
        }

        val spanCount = 3
        val gridLayout = GridLayoutManager(requireContext(), spanCount)

        with(binding.rvPhotos) {
            this.layoutManager = gridLayout
            this.adapter = adapterPhotos
        }

        binding.rbtnBodyWorks.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                maintenanceViewModel.setTypeWorks(RepairType.BODY_WORKS)
            }
        }

        binding.rbtnLocksmithWorks.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                maintenanceViewModel.setTypeWorks(RepairType.LOCKSMITH_WORKS)
            }
        }

        binding.rbtnMaintenance.setOnCheckedChangeListener { compoundButton, state ->
            if (state) {
                maintenanceViewModel.setTypeWorks(RepairType.MAINTENANCE)
            }
        }

        binding.layoutOpenCamera.setOnClickListener {
            takePhoto()
        }

        binding.layoutOpenGallery.setOnClickListener {
            pickImage()
        }

        binding.btnNext.setOnClickListener {
            maintenanceViewModel.collectMaintenanceParams()
        }

        carActionViewModel.getActionTitle()?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi(adapterPhotos)
    }

    private fun subscribeUi(adapter: PhotoAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            maintenanceViewModel.readyMaintenanceParams.collectLatest { isReady ->
                binding.btnNext.isEnabled = isReady
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            maintenanceViewModel.typeWorks.collectLatest { typeWorks ->
                typeWorks?.let { type ->
                    setViewsVisibility(type)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            maintenanceViewModel.uriPhotosMaintenance.collectLatest { photoItems ->
                if (photoItems == null) {
                    binding.layoutAddPhotos.visibility = View.GONE
                } else {
                    adapter.submitList(photoItems)
                    val masAmountPhotosAdded =
                        photoItems.size >= maintenanceViewModel.getPhotosMaxAmount()
                    binding.layoutAddPhotos.visibility =
                        if (masAmountPhotosAdded) View.GONE else View.VISIBLE

                    binding.tvMaxAmountPhotosWasAdded.visibility =
                        if (masAmountPhotosAdded) View.VISIBLE else View.GONE

                    binding.layoutLoadedPhotos.visibility =
                        if (photoItems.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            maintenanceViewModel.maintenanceTypes.collectLatest { types ->
                renderMaintenanceTypes(types)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            maintenanceViewModel.state.collectLatest { state ->
                when (state) {
                    is MaintenanceViewModel.MaintenanceState.Empty -> {}
                    is MaintenanceViewModel.MaintenanceState.Loading -> {
                        setLoadingState(true)
                    }
                    is MaintenanceViewModel.MaintenanceState.Success -> {
                        carActionViewModel.setRepairParams(state.maintenanceParams)
                        setLoadingState(false)
                    }
                    is MaintenanceViewModel.MaintenanceState.Error -> {
                        showErrorDialog(getString(state.resError))
                        setLoadingState(false)
                    }
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

        binding.rvPhotos.isEnabled = !isLoading
        binding.layoutOpenCamera.isEnabled = !isLoading
        binding.rbtnBodyWorks.isEnabled = !isLoading
        binding.rbtnLocksmithWorks.isEnabled = !isLoading
        binding.rbtnMaintenance.isEnabled = !isLoading
        binding.rbtnGroupTypeMaintenance.isEnabled = !isLoading
        binding.layoutLoadedPhotos.isEnabled = !isLoading
        binding.layoutMaintenanceItems.isEnabled = !isLoading
        binding.layoutOpenGallery.isEnabled = !isLoading
        binding.btnNext.isEnabled = !isLoading
    }

    private fun setViewsVisibility(typeRepair: RepairType) {
        when (typeRepair) {
            RepairType.BODY_WORKS -> {
                binding.layoutAddPhotos.visibility =
                    if (maintenanceViewModel.isMaximumPhotosLoaded()) View.GONE else View.VISIBLE
                binding.layoutMaintenance.visibility = View.GONE
                binding.btnNext.isEnabled = maintenanceViewModel.isSomePhotosLoaded()
                binding.layoutLoadedPhotos.visibility =
                    if (maintenanceViewModel.isSomePhotosLoaded()) View.VISIBLE else View.GONE
                binding.tvAddPhotosTitle.text =
                    getString(
                        R.string.required_add_photos,
                        maintenanceViewModel.getPhotosMaxAmount()
                    )
            }
            RepairType.LOCKSMITH_WORKS -> {
                binding.layoutAddPhotos.visibility =
                    if (maintenanceViewModel.isMaximumPhotosLoaded()) View.GONE else View.VISIBLE
                binding.layoutMaintenance.visibility = View.GONE
                binding.btnNext.isEnabled = true
                binding.layoutLoadedPhotos.visibility =
                    if (maintenanceViewModel.isSomePhotosLoaded()) View.VISIBLE else View.GONE
                binding.tvAddPhotosTitle.text =
                    getString(
                        R.string.add_photos_if_need,
                        maintenanceViewModel.getPhotosMaxAmount()
                    )
            }
            RepairType.MAINTENANCE -> {
                binding.layoutAddPhotos.visibility = View.GONE
                binding.layoutLoadedPhotos.visibility = View.GONE
                binding.layoutMaintenance.visibility = View.VISIBLE
                binding.btnNext.isEnabled = maintenanceViewModel.getMaintenanceType() != null
            }
        }
    }

    private fun hasCameraPermission() = maintenanceViewModel.hasCameraPermissions()

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
        //takePhoto()
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

        val uri = maintenanceViewModel.createImageFileUri()

        if (uri != null) {
            maintenanceViewModel.setPhotoUri(uri)
            takePictureLauncher.launch(uri)
        }
    }

    private fun pickImage() {
        if (!hasCameraPermission()) {
            requestPermissions()
            return
        }

        pickImagesLauncher.launch("image/*")
    }

    private fun renderMaintenanceTypes(maintenanceItems: List<MaintenanceItem>) {
        binding.layoutMaintenanceItems.removeAllViews()
        maintenanceItems.forEach { item ->
            val maintenanceBinding = ItemMaintenanceBinding.inflate(
                layoutInflater,
                binding.layoutMaintenanceItems,
                false
            )

            val itemBackgroundColor = MaterialColors.getColor(
                requireContext(),
                R.attr.background_primary_color,
                Color.GRAY
            )

            val itemBackgroundSelectedColor = MaterialColors.getColor(
                requireContext(),
                R.attr.text_extra_color,
                Color.GRAY
            )

            maintenanceBinding.tvMaintenanceTitle.text =
                getString(R.string.maintenance_title, item.id)


            maintenanceBinding.root.setOnClickListener {
                binding.layoutMaintenanceItems.forEach {
                    it.setBackgroundColor(itemBackgroundColor)
                }
                maintenanceBinding.layout.setBackgroundColor(itemBackgroundSelectedColor)
                maintenanceViewModel.setMaintenanceType(item.type)
            }

            binding.layoutMaintenanceItems.addView(maintenanceBinding.root)
        }
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}