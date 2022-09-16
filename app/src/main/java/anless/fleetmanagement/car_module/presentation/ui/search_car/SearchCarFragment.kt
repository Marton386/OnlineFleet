package anless.fleetmanagement.car_module.presentation.ui.search_car

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.utils.Constants
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.ui.MainViewModel
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.databinding.FragmentSearchCarBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchCarFragment : Fragment(R.layout.fragment_search_car) {

    companion object {
        const val TAG = "SearchCarFragment"
    }

    private val searchCarViewModel: SearchCarViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSearchCarBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchCarBinding.bind(view)
        binding.layoutScanQrcode.visibility = View.GONE
        AndroidUtils.openKeyboard(binding.etSearchCar)

        val carAdapter = CarAdapter { selectedCar ->
            if (carActionViewModel.isCarSelected()) {
                carActionViewModel.unselectCar()
            }
            selectIdCar(selectedCar.carInfo.id)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        with(binding.rvCars) {
            this.layoutManager = layoutManager
            this.adapter = carAdapter
        }

        binding.etSearchCar.addTextChangedListener { text ->
            val partLicensePlate = text.toString().trim()

            if (mainViewModel.hasHash()) {
                searchCarViewModel.setSearchPart(partLicensePlate)
                //binding.layoutScanQrcode.visibility = if (partLicensePlate.isEmpty())
                //    View.VISIBLE else View.GONE
            } else {
                if (text.toString().isNotEmpty()) {
                    text?.let { txt ->
                        txt.clear()
                        //binding.layoutScanQrcode.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.btnAddNewCar.setOnClickListener {
            addNewCar()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.relocations_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.mi_relocations -> {
                        findNavController().navigate(
                            SearchCarFragmentDirections.actionSearchCarFragmentToRelocationsFragment()
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        carActionViewModel.clearAll()

        (activity as MainActivity).setTitle(getString(R.string.search_car))
        (activity as MainActivity).setSubTitle(null)

        subscribeUi(carAdapter)
    }

    private fun subscribeUi(adapter: CarAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchCarViewModel.loading.collectLatest { isLoading ->
                if (isLoading) {
                    binding.tvCarNoFound.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchCarViewModel.errorMessage.collectLatest { resError ->
                showErrorDialog(getString(resError))
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchCarViewModel.cars.collectLatest { cars ->
                adapter.submitList(cars)

                binding.tvCarNoFound.visibility =
                    if (cars.isEmpty() && searchCarViewModel.isLengthEnoughForSearch()
                    ) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchCarViewModel.readyAddNewCar.collectLatest { isReady ->
                binding.btnAddNewCar.visibility = if (isReady) View.VISIBLE else View.GONE
            }
        }
    }

    private fun selectIdCar(idCar: Int) {
        carActionViewModel.setCarId(idCar)
        val action = SearchCarFragmentDirections.actionSearchCarFragmentToCarDetailsFragment()
        findNavController().navigate(action)
    }

    private fun addNewCar() {
        selectIdCar(Constants.ID_NEW_CAR)
        carActionViewModel.setLicensePlate(searchCarViewModel.getSearchPart())
    }
}