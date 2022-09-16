package anless.fleetmanagement.station_module.domain.repository

import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.domain.model.ScheduleItem

interface StationRepository {
    suspend fun getStations(): Result<List<Station>>

    suspend fun getCurrentSchedule(): Result<List<ScheduleItem>>
}