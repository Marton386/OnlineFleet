package anless.fleetmanagement.station_module.presentation.ui.schedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import anless.fleetmanagement.R
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.databinding.FragmentScheduleBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScheduleFragment : Fragment(R.layout.fragment_schedule) {

    companion object {
        const val TAG = "ScheduleFragment"
    }

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentScheduleBinding.bind(view)

        binding.swipeRefresh.setOnRefreshListener {
            scheduleViewModel.getCurrentSchedule()
        }

        val scheduleAdapter = ScheduleAdapter{}

        val layoutManager = LinearLayoutManager(requireContext())
        with(binding.rvSchedule) {
            this.layoutManager = layoutManager
            this.adapter = scheduleAdapter
            addItemDecoration(MarginItemDecoration(requireContext()))
        }

        (activity as MainActivity).setTitle(getString(R.string.schedule))
        (activity as MainActivity).setSubTitle(null)

        subscribeUi(scheduleAdapter)
    }

    private fun subscribeUi(adapter: ScheduleAdapter) {
        lifecycleScope.launchWhenStarted {
            scheduleViewModel.loading.collectLatest { isLoading ->
                setLoadingState(isLoading)
            }
        }

        lifecycleScope.launchWhenStarted {
            scheduleViewModel.schedule.collectLatest { scheduleList ->
                adapter.submitList(scheduleList)
                binding.rvSchedule.visibility = View.VISIBLE
            }
        }

        lifecycleScope.launchWhenStarted {
            scheduleViewModel.errorRes.collectLatest { resError ->
                resError?.let { error ->
                    val errorMessage = getString(error)
                    showErrorDialog(errorMessage)
                    binding.tvScheduleInfo.text = errorMessage
                    binding.tvScheduleInfo.visibility = View.VISIBLE
                    binding.rvSchedule.visibility = View.GONE
                }
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.swipeRefresh.isRefreshing = isLoading
        if (!isLoading) {
            if (scheduleViewModel.isScheduleEmpty()) {
                binding.tvScheduleInfo.visibility = View.VISIBLE
                binding.tvScheduleInfo.text = getString(R.string.schedule_is_empty)
            } else {
                binding.tvScheduleInfo.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scheduleViewModel.getCurrentSchedule()
    }
}