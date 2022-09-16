package anless.fleetmanagement.core.app.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.databinding.ActivityMainBinding
import anless.fleetmanagement.user_module.presentation.ui.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    private val profileViewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment,
                R.id.searchCarFragment,
                R.id.qrCodeFragment,
                R.id.scheduleFragment,
            )
        )

        binding.bottomNavigation.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.carDetailsFragment, R.id.relocationsFragment, R.id.monitoringFragment, R.id.reservationFragment, R.id.mileageFragment, R.id.fuelAndCleanFragment, R.id.maintenanceFragment, R.id.invoiceFragment, R.id.priceFragment, R.id.washingFragment, R.id.contractorFragment, R.id.litersFragment, R.id.actFragment, R.id.stopDaysFragment, R.id.tireFragment, R.id.commentFragment, R.id.dropOffPlaceFragment, R.id.overpriceFragment, R.id.sendActionFragment, R.id.sureCarFragment -> {
                    selectCarActionIcon()
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.stationFragment -> {
                    if (navController.currentBackStackEntry?.destination?.id != R.id.profileFragment) {
                        selectCarActionIcon()
                    }

                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                R.id.loginFragment -> {
                    binding.toolbar.navigationIcon = null
                    binding.bottomNavigation.visibility = View.GONE
                }
                else -> binding.bottomNavigation.visibility = View.VISIBLE
            }
            mainViewModel.setCurrentDestination(destination.id)
        }

        binding.toolbar.setNavigationOnClickListener()
        {
            this.onBackPressed()
        }

        subscribeUi()
    }

    private fun subscribeUi() {
        mainViewModel.hash.observe(this) { userHash ->
            if (userHash == null) {
                navController.popBackStack(R.id.profileFragment, false)
            } else {
                profileViewModel.checkSession()
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.shiftIsOpen.collectLatest { shiftIsOpen ->
                setIconsState(shiftIsOpen)
            }
        }

        lifecycleScope.launchWhenStarted {
            profileViewModel.state.collectLatest { state ->
                when (state) {
                    is ProfileViewModel.ProfileState.Empty -> {}
                    is ProfileViewModel.ProfileState.Loading -> {
                        setIconsState(false)
                    }
                    is ProfileViewModel.ProfileState.Success -> {
                        setIconsState(state.shift != null)

                        mainViewModel.setShift(state.shift)

                        if (state.shift == null) {
                            if (navController.currentDestination?.id != R.id.profileFragment) {
                                navController.popBackStack(
                                    R.id.profileFragment,
                                    inclusive = false,
                                    saveState = false
                                )
                            }
                        } else {
                            selectSearchCarItem()
                            mainViewModel.setUserTimezone(profileViewModel.getUserTimezone())
                        }

                    }
                    is ProfileViewModel.ProfileState.Error -> {
                        setIconsState(false)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.currentThemeIsNight.collectLatest { isNightTheme ->
                if (isNightTheme != null) {
                    setTheme(isNightTheme)
                }
            }
        }

    }

    private fun selectCarActionIcon() {
        if (!binding.bottomNavigation.menu.findItem(R.id.searchCarFragment).isChecked) {
            binding.bottomNavigation.menu.findItem(R.id.searchCarFragment).isChecked = true
        }
    }

    private fun selectSearchCarItem() {
        binding.bottomNavigation.selectedItemId = R.id.searchCarFragment
    }

    fun openRelocations() {
        selectSearchCarItem()
        navController.navigate(R.id.action_global_relocationsFragment)
    }

    fun openCarDetails() {
        selectSearchCarItem()
        navController.navigate(R.id.action_global_carDetailsFragment)
    }

    fun openMileage() {
        selectSearchCarItem()
        navController.navigate(R.id.action_global_mileageFragment)
    }

    fun openDropOffPlace() {
        selectSearchCarItem()
        navController.navigate(R.id.action_global_dropOffPlaceFragment)
    }

    fun openSureDrop() {
        selectSearchCarItem()
        navController.navigate(R.id.action_global_sureCarFragment)
    }

    private fun setIconsState(isEnabled: Boolean) {
        binding.bottomNavigation.menu.findItem(R.id.searchCarFragment).isEnabled =
            isEnabled
        binding.bottomNavigation.menu.findItem(R.id.qrCodeFragment).isEnabled =
            isEnabled
        binding.bottomNavigation.menu.findItem(R.id.scheduleFragment).isEnabled =
            isEnabled
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setSubTitle(subTitle: String?) {
        if (subTitle.isNullOrBlank()) {
            binding.tvSubTitle.visibility = View.GONE
        } else {
            binding.tvSubTitle.text = subTitle
            binding.tvSubTitle.visibility = View.VISIBLE
        }
    }

    private fun setTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }

    /*private fun isClearAction(destination: Int): Boolean {
        if (destination == mainViewModel.getCurrentDestination()) {
            navController.navigate(R.id.action_global_searchCarFragment)
            return true
        }
        return false
    }*/
}