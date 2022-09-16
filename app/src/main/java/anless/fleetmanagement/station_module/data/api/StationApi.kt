package anless.fleetmanagement.station_module.data.api

import anless.fleetmanagement.station_module.data.model.ScheduleDTO
import anless.fleetmanagement.station_module.data.model.StationDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface StationApi {
    @FormUrlEncoded
    @POST("/php/app/stations/all_default_stations.php")
    suspend fun getStations(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<List<StationDTO>>

    @FormUrlEncoded
    @POST("/php/app/schedule_ext/current_schedule.php")
    suspend fun getCurrentSchedule(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<ScheduleDTO>
}