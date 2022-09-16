package anless.fleetmanagement.car_module.presentation.ui.monitoring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.CarPosition
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import anless.fleetmanagement.core.utils.DateFormatter
import anless.fleetmanagement.databinding.FragmentMonitoringBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.math.RoundingMode
import java.text.DecimalFormat


@AndroidEntryPoint
class MonitoringFragment : Fragment(R.layout.fragment_monitoring) {

    companion object {
        const val TAG = "MonitoringFragment"
    }

    private val monitoringViewModel: MonitoringViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var mapController: IMapController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMonitoringBinding.bind(view)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        if (!isDeviceWithGoogleServices()) {
            Configuration.getInstance().userAgentValue = requireContext().packageName
            mapFragment.view?.visibility = View.GONE
            binding.map.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            binding.map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
            binding.map.setMultiTouchControls(true)
            mapController = binding.map.controller
            mapController.setZoom(19.0)
        }
        else {
            binding.map.visibility = View.GONE
            mapFragment.getMapAsync { map ->
                googleMap = map
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                googleMap.setOnCameraMoveListener {
                    monitoringViewModel.setMapZoom(googleMap.cameraPosition.zoom)
                }
            }
        }


        binding.layoutRefresh.setOnClickListener {
            monitoringViewModel.getCarPosition()
        }

        (activity as MainActivity).setTitle(getString(R.string.monitoring))

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
                    monitoringViewModel.setCarId(idCar)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            monitoringViewModel.carPosition.collect { carPosition ->
                carPosition?.let { position ->
                    showCarPositionInfo(position)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            monitoringViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            monitoringViewModel.errorRes.collectLatest { resError ->
                resError?.let { error ->
                    val errorMessage = getString(error)
                    showErrorDialog(errorMessage)
                    binding.tvError.text = errorMessage
                    binding.tvLastUpdate.text = ""
                    binding.tvError.visibility = View.VISIBLE
                    binding.layoutMonitororingInfo.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            monitoringViewModel.carWithoutEquipment.collect { withoutEquipment ->
                withoutEquipment?.let { noEquipment ->
                    binding.tvError.text = getString(R.string.not_available_for_this_vehicle)

                    if (noEquipment) {
                        binding.tvError.visibility = View.VISIBLE
                        binding.layoutMonitororingInfo.visibility = View.GONE
                        binding.layoutRefresh.visibility = View.GONE
                    } else {
                        binding.tvError.visibility = View.GONE
                        binding.layoutMonitororingInfo.visibility = View.VISIBLE
                        binding.layoutRefresh.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap =
            Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.imgRefresh.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        binding.layoutRefresh.isEnabled = !isLoading
    }

    private fun showCarPositionInfo(carPosition: CarPosition) {
        carPosition.lastUpdate?.let { lastUpdate ->
            binding.tvLastUpdate.text = getString(
                R.string.last_update_ago,
                DateFormatter.getTimeAgoString(requireContext(), lastUpdate)
            )
        }

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.DOWN

        binding.tvSpeed.text =
            getString(R.string.km_in_hour, decimalFormat.format(carPosition.speed))
        binding.tvVoltage.text = getString(R.string.volt, decimalFormat.format(carPosition.voltage))
        binding.layoutMonitororingInfo.visibility = View.VISIBLE
        setCarPositionOnMap(carPosition.location)
    }

    private fun setCarPositionOnMap(location: LatLng) {
        if (isDeviceWithGoogleServices()) {
            if (!this::googleMap.isInitialized) {
                return
            }
            googleMap.clear()

            val googlePlex = CameraPosition.builder()
                .target(location)
                .zoom(monitoringViewModel.getMapZoom())
                .bearing(0f)
                .tilt(45f)
                .build()

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null)

            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(carActionViewModel.getCarTitle() ?: getString(R.string.car))
                    .icon(bitmapDescriptorFromVector(activity, R.drawable.ic_car_marker))
            )
        }
        else {
            val myMarker = Marker(binding.map)
            myMarker.image = resources.getDrawable(R.drawable.ic_car_marker_2, null)
            myMarker.title = carActionViewModel.getCarTitle() ?: getString(R.string.car)
            myMarker.icon = resources.getDrawable(R.drawable.ic_car_marker, null)
            binding.map.overlays.add(myMarker)
            val point = GeoPoint(location.latitude, location.longitude)
            myMarker.position = point
            mapController.animateTo(point)
            binding.map.invalidate()
        }
    }

    private fun isDeviceWithGoogleServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        return googleApiAvailability.isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS
    }

    override fun onResume() {
        super.onResume()
        monitoringViewModel.getCarPosition()
    }

    override fun onDestroy() {
        monitoringViewModel.clearData()
        super.onDestroy()
    }
}