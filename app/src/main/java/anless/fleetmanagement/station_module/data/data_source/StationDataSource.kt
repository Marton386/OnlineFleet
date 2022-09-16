package anless.fleetmanagement.station_module.data.data_source

import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface StationDataSource {

    suspend fun getStations(sourceInfo: SourceInfo): Result<List<Station>>

    suspend fun getCurrentSchedule(sourceInfo: SourceInfo): Result<List<ScheduleItem>>
}