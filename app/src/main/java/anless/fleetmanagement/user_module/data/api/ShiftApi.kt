package anless.fleetmanagement.user_module.data.api

import anless.fleetmanagement.user_module.data.model.ShiftDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ShiftApi {
    @FormUrlEncoded
    @POST("/php/app/shift/shift.php")
    suspend fun getCurrentShift(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<ShiftDTO>

    @FormUrlEncoded
    @POST("/php/app/shift/open.php")
    suspend fun openShift(
        @Field("hash") hash: String,
        @Field("station_id") idStation: Int,
        @Field("lat") latitude: Double,
        @Field("lon") longitude: Double,
        @Field("push_token") notificationToken: String,
        @Field("ver") appVersion: Int,
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/shift/close.php")
    suspend fun closeShift(
        @Field("hash") hash: String,
        @Field("lat") latitude: Double,
        @Field("lon") longitude: Double,
        @Field("ver") appVersion: Int,
    ): Response<ResponseBody>
}