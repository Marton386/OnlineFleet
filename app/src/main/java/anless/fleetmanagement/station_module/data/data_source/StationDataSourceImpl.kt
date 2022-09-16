package anless.fleetmanagement.station_module.data.data_source

import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.data.api.StationApi
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import retrofit2.HttpException
import java.net.UnknownHostException

class StationDataSourceImpl(
    private val stationApi: StationApi
) : StationDataSource {
    override suspend fun getStations(sourceInfo: SourceInfo): Result<List<Station>> =
        try {
            val response = stationApi.getStations(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(response.body()!!.map { stationDTO ->
                    stationDTO.toStation()
                })
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getCurrentSchedule(sourceInfo: SourceInfo): Result<List<ScheduleItem>> =
        try {
            val response = stationApi.getCurrentSchedule(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toSchedule())
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ErrorCodes.EXCEPTION)
        }
}