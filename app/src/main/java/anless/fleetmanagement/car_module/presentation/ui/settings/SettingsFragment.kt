package anless.fleetmanagement.car_module.presentation.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.R
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.app.presentation.ui.MainViewModel
import anless.fleetmanagement.databinding.FragmentSettingsBinding
import anless.fleetmanagement.core.app.presentation.utils.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    companion object {
        const val TAG = "SettingsFragment"
    }

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.swAutoLogout.setOnCheckedChangeListener { _, state ->
            settingsViewModel.setAutoLogoutState(autoLogoutState = state)
        }

        binding.swDarkTheme.setOnCheckedChangeListener { _, state ->
            mainViewModel.changeTheme(isNightTheme = state)
        }

        binding.tvLogout.setOnClickListener {
            val confirmDialog = showConfirmDialog(getString(R.string.logout_confirm))
            confirmDialog?.let { dialog ->
                dialog.findViewById<Button>(R.id.btn_ok)?.setOnClickListener {
                    mainViewModel.logout()
                    dialog.dismiss()
                }
            }
        }

        binding.tvAppVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        (activity as MainActivity).setTitle(getString(R.string.settings))

        subscribeUi()
    }

    private fun subscribeUi(){
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            settingsViewModel.autoLogout.collectLatest { autoLogoutState ->
                val currentState = binding.swAutoLogout.isChecked
                if (currentState != autoLogoutState) {
                    binding.swAutoLogout.isChecked = autoLogoutState
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            mainViewModel.currentThemeIsNight.collectLatest { isNightTheme ->
                if (isNightTheme != null) {
                    val currentState = binding.swDarkTheme.isChecked
                    if (currentState != isNightTheme) {
                        binding.swDarkTheme.isChecked = isNightTheme
                    }
                }
            }
        }
    }
}