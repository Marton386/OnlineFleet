package anless.fleetmanagement.user_module.presentation.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.relocations.RelocationsViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.ui.MainViewModel
import anless.fleetmanagement.core.app.presentation.utils.showConfirmDialog
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.core.app.presentation.utils.showMessageDialog
import anless.fleetmanagement.core.app.presentation.utils.showToast
import anless.fleetmanagement.core.utils.DateFormatter
import anless.fleetmanagement.core.utils.FragmentHandleKeys
import anless.fleetmanagement.databinding.FragmentProfileBinding
import anless.fleetmanagement.user_module.domain.model.Shift
import anless.fleetmanagement.user_module.domain.model.User
import anless.fleetmanagement.user_module.presentation.utils.*
import com.bumptech.glide.Glide
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val relocationsViewModel: RelocationsViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager

    private val locationListener = LocationListener { location ->
        setLocation(location)
    }

    private val openAppUpdateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        bindProgressButton(binding.btnOpenShift)
        bindProgressButton(binding.btnCloseShift)

        binding.tvRating.setOnClickListener {
            showToast(getString(R.string.this_is_your_rating))
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(
            FragmentHandleKeys.idStation
        )?.observe(viewLifecycleOwner) { idStation ->
            profileViewModel.setIdStation(idStation)
        }

        binding.btnOpenShift.setOnClickListener {
            getLocation()
            //getLocation()
        }

        binding.btnCloseShift.setOnClickListener {
            val confirmDialog = showConfirmDialog(getString(R.string.confirm_shift_ending))
            confirmDialog?.let { dialog ->
                dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
                    dialog.dismiss()
                    //getLocation()
                    getLocation()
                }
            }
        }

        binding.imgRefresh.setOnClickListener {
            profileViewModel.checkSession()
        }

        binding.layoutOpenedRelocations.setOnClickListener {
            (activity as MainActivity).openRelocations()
        }

        binding.layoutUpdateIsAvailable.setOnClickListener {
            val linkAppInStore =
                "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

            val openStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkAppInStore))
            openAppUpdateLauncher.launch(openStoreIntent)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_settings -> {
                        findNavController().navigate(
                            ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        profileViewModel.checkRequireUpdateApp()

        (activity as MainActivity).setTitle(getString(R.string.account))
        (activity as MainActivity).setSubTitle(null)

        subscribeUi()
    }


    private fun subscribeUi() {
        mainViewModel.hash.observe(viewLifecycleOwner) { userHash ->
            if (userHash == null) {
                if (profileViewModel.isSessionNeedClose()) {
                    val alertDialog = showErrorDialog(getString(R.string.your_session_is_closed))

                    alertDialog?.let { dialog ->
                        dialog.setOnDismissListener {
                            profileViewModel.sessionClosed()
                            goToLoginScreen()
                        }
                    }
                } else {
                    goToLoginScreen()
                }

            } else {
                if (profileViewModel.isUserEmpty()) {
                    profileViewModel.checkSession()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            profileViewModel.state.collectLatest { state ->
                when (state) {
                    is ProfileViewModel.ProfileState.Success -> {
                        state.shift?.let { relocationsViewModel.checkRelocations() }
                        setShiftInfo(state.shift)

                        binding.imgRefresh.visibility = View.INVISIBLE
                        binding.layoutRefresh.visibility = View.GONE
                        shiftLoading(false)

                    }
                    is ProfileViewModel.ProfileState.Error -> {
                        shiftLoading(false)

                        val error = getString(state.codeError)
                        showErrorDialog(error)
                        binding.tvUpdatingInfo.text = error
                        binding.imgRefresh.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                        binding.layoutRefresh.visibility = View.VISIBLE
                    }
                    is ProfileViewModel.ProfileState.Loading -> {
                        shiftLoading(true)
                        binding.imgRefresh.visibility = View.INVISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            profileViewModel.user.collectLatest { user ->
                if (user != null) {
                    binding.tvShiftState.visibility = View.VISIBLE
                    setUserInfo(user)
                } else {
                    binding.tvShiftState.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            profileViewModel.requiredUpdateApp.collectLatest { updateIsRequire ->
                binding.layoutUpdateIsAvailable.visibility =
                    if (updateIsRequire) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            relocationsViewModel.relocations.collectLatest { relocations ->
                binding.layoutOpenedRelocations.visibility = if (relocations == null) {
                    View.GONE
                } else {
                    if (relocations.isEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
            }
        }
    }

    private fun shiftLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.imgRefresh.visibility = View.INVISIBLE
            binding.layoutShift.visibility = View.GONE
            binding.tvShiftState.visibility = View.GONE
            binding.tvUpdatingInfo.text = getString(R.string.loading)
            binding.layoutRefresh.visibility = View.VISIBLE

            if (profileViewModel.isUserEmpty()) {
                binding.tvRating.visibility = View.GONE
            }
        } else {
            binding.layoutShift.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.layoutRefresh.visibility = View.GONE
            showShiftButtonsEnabled(true)
        }

    }

    private fun setShiftInfo(shift: Shift?) {
        if (shift == null) {
            binding.btnOpenShift.visibility = View.VISIBLE
            binding.btnCloseShift.visibility = View.GONE
            binding.tvShiftState.text = getString(R.string.no_opened_shift)
            binding.tvShiftState.visibility = View.VISIBLE
            binding.tvStation.visibility = View.GONE
            binding.layoutRefresh.visibility = View.GONE
            binding.layoutOpenedRelocations.visibility = View.GONE
            return
        }

        val locale = Locale.getDefault()

        if (locale.language == "ru") {
            binding.tvStation.text = shift.station.nameRu
        } else {
            binding.tvStation.text = shift.station.nameEn
        }

        binding.tvShiftState.text = getString(
            R.string.shift_was_open,
            DateFormatter.getTimeAgoString(requireContext(), shift.startTime)
        )

        binding.btnOpenShift.visibility = View.GONE
        binding.btnCloseShift.visibility = View.VISIBLE
        binding.tvStation.visibility = View.VISIBLE
        binding.tvShiftState.visibility = View.VISIBLE
        binding.layoutRefresh.visibility = View.GONE
    }

    private fun setUserInfo(user: User) {
        binding.tvUserName.text = user.name
        binding.tvPost.text = user.post
        binding.tvRating.text = "${user.rating}"
        binding.tvRating.visibility = View.VISIBLE

        Glide
            .with(this)
            .load(Constants.URL_AVATAR + user.avatarFilename)
            .placeholder(R.drawable.avatar_placeholder)
            .circleCrop()
            .into(binding.imgAvatar)
    }

    private fun showShiftButtonsEnabled(state: Boolean) {
        binding.btnOpenShift.isEnabled = state
        binding.btnCloseShift.isEnabled = state
        if (state) {
            binding.btnOpenShift.hideProgress(getString(R.string.open_shift))
            binding.btnCloseShift.hideProgress(getString(R.string.close_shift))
        } else {
            binding.btnOpenShift.showProgress()
            binding.btnCloseShift.showProgress()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationGPServices() {
        val tokenCancelLocation = CancellationTokenSource().token
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenCancelLocation)
            .addOnSuccessListener { location ->
                if (location == null) {
                    showErrorDialog(getString(R.string.cant_get_location))
                    showShiftButtonsEnabled(true)
                } else {
                    setLocation(location)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                Firebase.crashlytics.recordException(exception)
                showMessageDialog(getString(R.string.cant_get_location))
                showShiftButtonsEnabled(true)
            }
    }

    private fun getLocationWithoutGoogleServices() {
        val minTimeSearchingMillis = 10 * 1000L
        val minDistanceM = 0F

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            minTimeSearchingMillis,
            minDistanceM,
            locationListener
        )

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            minTimeSearchingMillis,
            minDistanceM,
            locationListener
        )
    }

    private fun setLocation(location: Location) {
        profileViewModel.setLocation(
            LatLng(location.latitude, location.longitude)
        )

        stopSearchingLocation()

        if (profileViewModel.isShiftOpen()) {
            profileViewModel.closeShift()
        } else {
            openStationScreen()
        }
    }

    private fun stopSearchingLocation() {
        locationManager.removeUpdates(locationListener)
    }

    private fun hasLocationPermission() = LocationUtility.hasLocationPermissions(requireContext())

    private fun getLocation() {
        showShiftButtonsEnabled(false)

        if (!hasLocationPermission()) {
            requestPermissions()
            return
        }

        if (isDeviceWithGoogleServices()) {
            getLocationGPServices()
        } else {
            getLocationWithoutGoogleServices()
        }
    }

    private fun isDeviceWithGoogleServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        return googleApiAvailability.isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS
    }

    private fun requestPermissions() {
        if (hasLocationPermission()) {
            return
        }

        EasyPermissions.requestPermissions(
            this,
            getString(R.string.need_access_location),
            Constants.REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getLocation()
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

    private fun openStationScreen() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToStationFragment())
    }

    private fun goToLoginScreen() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

}
