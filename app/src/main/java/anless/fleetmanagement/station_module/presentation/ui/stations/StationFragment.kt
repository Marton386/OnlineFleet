package anless.fleetmanagement.station_module.presentation.ui.stations

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.core.utils.FragmentHandleKeys
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentStationsBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class StationFragment : Fragment(R.layout.fragment_stations) {

    companion object {
        private const val TAG = "StationFragment"
    }

    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private val stationsViewModel: StationViewModel by viewModels()
    private var _binding: FragmentStationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStationsBinding.bind(view)

        binding.etSearchStation.addTextChangedListener { searchStation ->
            //stationsViewModel.filterStations(searchStation.toString())
            stationsViewModel.setSearchStation(searchStation.toString())
        }

        AndroidUtils.openKeyboard(binding.etSearchStation)

        val stationAdapter = StationAdapter { selectedStation ->
            //selectStation(selectedStation.id)
            stationsViewModel.setStation(selectedStation)
        }

        val layoutManager = LinearLayoutManager(requireContext())

        with(binding.rvStations) {
            this.layoutManager = layoutManager
            this.adapter = stationAdapter
        }

        binding.layoutRefresh.setOnClickListener {
            stationsViewModel.loadStations()
        }

        binding.btnNext.setOnClickListener {
            stationsViewModel.getStation()?.let { station ->
                selectStation(station.id)
            }
        }

        if (carActionViewModel.isCarAction()) {
            val resTitle = carActionViewModel.getActionTitle()
            resTitle?.let { title ->
                (activity as MainActivity).setTitle(getString(title))
            }

            val car = carActionViewModel.getCar()
            car?.carInfo?.licensePlate?.let { licensePlate ->
                (activity as MainActivity).setSubTitle(licensePlate.uppercase())
            }

            carActionViewModel.getDefaultStationId()?.let { idStation ->
                stationsViewModel.setStationId(idStation)
            }
        } else {
            (activity as MainActivity).setTitle(getString(R.string.open_shift))
            (activity as MainActivity).setSubTitle(null)
        }

        subscribeUi(stationAdapter)
    }

    private fun subscribeUi(stationAdapter: StationAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            stationsViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            stationsViewModel.stations.collectLatest { stationsList ->
                stationAdapter.submitList(stationsList)

                if (stationsList.isEmpty() || stationsList.size > 1) {
                    stationsViewModel.clearSelectedStation()
                }

                if (stationsList.size == 1) {
                    //selectStation(stationsList[0].id)
                    stationsViewModel.setStation(stationsList[0])
                }

                binding.tvStationNotFound.visibility =
                    if (stationsList.isEmpty() && binding.etSearchStation.text.toString()
                            .isNotEmpty()
                    ) View.VISIBLE else View.GONE
                binding.layoutStations.visibility = View.VISIBLE
                binding.layoutRefresh.visibility = View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            stationsViewModel.error.collectLatest { resError ->
                val errorMessage = getString(resError)
                showErrorDialog(errorMessage)
                binding.tvLoadingInfo.text = errorMessage
                binding.tvLoadingInfo.visibility = View.VISIBLE
                binding.imgRefresh.visibility = View.VISIBLE
                binding.layoutStations.visibility = View.GONE
                binding.layoutRefresh.visibility = View.VISIBLE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            stationsViewModel.station.collectLatest { station ->
                if (station == null) {
                    binding.btnNext.isEnabled = false
                    binding.btnNext.visibility = View.GONE
                } else {
                    binding.btnNext.isEnabled = true
                    binding.btnNext.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            stationsViewModel.searchStation.collectLatest { filter ->
                stationsViewModel.filterStations()

                if (filter != binding.etSearchStation.text.toString()) {
                    binding.etSearchStation.setText(filter)
                    binding.etSearchStation.setSelection(filter.length)
                }
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)

    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layoutStations.visibility = View.INVISIBLE
            binding.tvLoadingInfo.text = getString(R.string.loading_stations)
            binding.imgRefresh.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            //binding.layout.visibility = View.VISIBLE
        }

        binding.btnNext.isEnabled = !isLoading
    }

    private fun selectStation(idStation: Int) {
        if (carActionViewModel.isCarAction()) {
            // is action with car
            carActionViewModel.setStationId(idStation)
        } else {
            // is opening shift
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                FragmentHandleKeys.idStation,
                idStation
            )

            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}