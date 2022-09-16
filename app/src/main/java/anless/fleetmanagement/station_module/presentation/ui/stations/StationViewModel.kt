package anless.fleetmanagement.station_module.presentation.ui.stations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.station_module.domain.usecase.GetStationsUseCase
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class StationViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase
) : ViewModel() {

    private var idStation: Int? = null

    private val _searchStation = MutableStateFlow(value = "")
    val searchStation = _searchStation.asStateFlow()

    private var stationsList: List<Station> = listOf()
    private val _stations = MutableStateFlow<List<Station>>(value = listOf())
    val stations = _stations.asStateFlow()

    private val _station = MutableStateFlow<Station?>(value = null)
    val station = _station.asStateFlow()

    private val _loading = MutableStateFlow<Boolean>(value = true)
    val loading = _loading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<Int>()
    val error = _errorMessage.asSharedFlow()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!_loading.value) {
                _loading.value = true
            }

            val result = getStationsUseCase()

            when (result) {
                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        stationsList = result.data
                        _stations.value = result.data
                        _loading.value = false

                        setDefaultStation()
                    }
                }
                is Result.Error -> {
                    _loading.value = false
                    getErrorByCode(result.errorCode)?.let { errorRes ->
                        _errorMessage.emit(errorRes)
                    }
                }
            }
        }
    }

    fun setStation(station: Station) {
        setFilterStationName(station)
        //filterStations()
        _station.value = station
    }

    fun setStationId(id: Int) {
        idStation = id
    }

    fun setSearchStation(filter: String) {
        _searchStation.value = filter
    }

    fun clearSelectedStation() {
        _station.value = null
    }

    fun getStation() = _station.value

    private fun setFilterStationName(station: Station) {
        _searchStation.value = if (Locale.getDefault().language == "ru") {
            station.nameRu
        } else {
            station.nameEn
        }
    }

    private fun setDefaultStation() {
        idStation?.let { stationId ->
            _stations.value.forEach { station ->
                if (station.id == stationId) {
                    setFilterStationName(station)
                }
            }
        }
    }

    fun filterStations() {
        val filter = _searchStation.value
        _stations.value = if (filter.isEmpty()) {
            stationsList
        } else {
            stationsList.filter { stationInList ->
                stationInList.nameEn.startsWith(filter, ignoreCase = true)
                        || stationInList.nameRu.contains(filter, ignoreCase = true)
                        || stationInList.shortCode.startsWith(filter, ignoreCase = true)
            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null//R.string.your_session_is_closed
            else -> R.string.error_load_data
        }
}