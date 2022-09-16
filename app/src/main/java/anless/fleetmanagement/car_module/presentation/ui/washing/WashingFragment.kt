package anless.fleetmanagement.car_module.presentation.ui.washing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.car_module.presentation.ui.simple_item.SimpleItemAdapter
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentWashingBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class WashingFragment : Fragment(R.layout.fragment_washing) {

    companion object {
        private const val TAG = "WashingFragment"
    }

    private val washingViewModel: WashingViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentWashingBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWashingBinding.bind(view)

        val carWashesAdapter = SimpleItemAdapter { carWash ->
            washingViewModel.setIdCarWash(carWash.id)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        with(binding.rvCarWashes) {
            this.layoutManager = layoutManager
            this.adapter = carWashesAdapter
        }

        binding.layoutRefresh.setOnClickListener {
            washingViewModel.getCarWashes()
        }

        carActionViewModel.getActionTitle()?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        subscribeUi(carWashesAdapter)
    }

    private fun subscribeUi(adapter: SimpleItemAdapter) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            washingViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        lifecycleScope.launchWhenStarted {
            washingViewModel.carWashes.collectLatest { carWashes ->
                carWashes?.let { washes ->
                    adapter.submitList(washes)
                    binding.layout.visibility = View.VISIBLE
                    binding.layoutRefresh.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            washingViewModel.idCarWash.collectLatest { idCarWash ->
                idCarWash?.let { id ->
                    carActionViewModel.setCarWashId(id)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            washingViewModel.errorRes.collectLatest { errorRes ->
                val errorMessage = getString(errorRes)
                showErrorDialog(errorMessage)
                binding.tvLoadingInfo.text = errorMessage
                binding.tvLoadingInfo.visibility = View.VISIBLE
                binding.imgRefresh.visibility = View.VISIBLE
                binding.layout.visibility = View.GONE
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.layout.visibility = View.INVISIBLE
            binding.imgRefresh.visibility = View.GONE
            binding.tvLoadingInfo.text = getString(R.string.loading_carwashes)
            binding.imgRefresh.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.tvLoadingInfo.text = ""
            //binding.layout.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}