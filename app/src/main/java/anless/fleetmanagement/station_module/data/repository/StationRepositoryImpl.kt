package anless.fleetmanagement.station_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.station_module.data.data_source.StationDataSource
import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.station_module.domain.repository.StationRepository
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class StationRepositoryImpl(
    private val stationDataSource: StationDataSource,
    val authorizationManager: AuthorizationManager
): StationRepository {
    override suspend fun getStations(): Result<List<Station>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return stationDataSource.getStations(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getCurrentSchedule(): Result<List<ScheduleItem>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return stationDataSource.getCurrentSchedule(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}