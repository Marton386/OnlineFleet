package anless.fleetmanagement.car_module.presentation.ui.relocations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.ui.search_car.CarAdapter
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.databinding.FragmentRelocationsBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class RelocationsFragment : Fragment(R.layout.fragment_relocations) {

    companion object {
        const val TAG = "RelocationsFragment"
    }

    private val relocationsViewModel: RelocationsViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentRelocationsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRelocationsBinding.bind(view)

        binding.swipeRefresh.setOnRefreshListener {
            relocationsViewModel.checkRelocations()
        }

        val adapter = CarAdapter { carItem ->
            carActionViewModel.setCarId(carItem.carInfo.id)
            //carActionViewModel.setActionEndRelocation()
            findNavController().navigate(RelocationsFragmentDirections.actionRelocationsFragmentToCarDetailsFragment())
            //(activity as MainActivity).openCarDetails()
        }

        val layoutManager = LinearLayoutManager(requireContext())
        with(binding.rvRelocations) {
            this.layoutManager = layoutManager
            this.adapter = adapter
        }

        (activity as MainActivity).setTitle(getString(R.string.relocations))
        (activity as MainActivity).setSubTitle(null)

        relocationsViewModel.checkRelocations()

        val resTitle = carActionViewModel.getActionTitle()
        resTitle?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: CarAdapter) {
        lifecycleScope.launchWhenStarted {
            relocationsViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        lifecycleScope.launchWhenStarted {
            relocationsViewModel.errorRes.collectLatest { resError ->
                resError?.let { error ->
                    val errorMessage = getString(error)
                    showErrorDialog(errorMessage)
                    binding.tvRelocationsInfo.text = errorMessage
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            relocationsViewModel.relocations.collectLatest { relocations ->
                if (relocations != null) {
                    if (relocations.isEmpty()) {
                        binding.tvRelocationsInfo.text =
                            getString(R.string.you_dont_have_open_relocations)
                    } else {
                        binding.tvRelocationsInfo.text = getString(R.string.your_open_relocations)

                        adapter.submitList(relocations.map { relocationItem ->
                            CarItem(
                                carInfo = Car.CarInfo(
                                    id = relocationItem.idCar,
                                    model = relocationItem.carModel,
                                    licensePlate = relocationItem.licensePlate
                                ),
                                stationInfo = Car.StationInfo(
                                    idStation = 0,
                                    codeStation = relocationItem.stationShortCode
                                )
                            )
                        })
                    }
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
 /*       if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvRelocations.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvRelocations.visibility = View.VISIBLE
        }*/

        binding.rvRelocations.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.swipeRefresh.isRefreshing = isLoading
    }
}