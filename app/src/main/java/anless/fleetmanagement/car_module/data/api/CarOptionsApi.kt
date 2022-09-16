package anless.fleetmanagement.car_module.data.api

import anless.fleetmanagement.car_module.data.model.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CarOptionsApi {
    @FormUrlEncoded
    @POST("/php/app/user/get_relocations.php")
    suspend fun getRelocations(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<List<RelocationDTO>>

    @FormUrlEncoded
    @POST("/php/app/vehicles/get_tires.php")
    suspend fun getCurrentTires(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("ver") appVersion: Int
    ): Response<TireTypeDTO>

    @FormUrlEncoded
    @POST("/php/app/book/check3.php")
    suspend fun getReservations(
        @Field("hash") hash: String,
        @Field("str") resNumber: String,
        @Field("vehicle_id") idCar: Int,
        @Field("ver") appVersion: Int
    ): Response<List<ReservationDTO>>

    @FormUrlEncoded
    @POST("/php/app/carwash/carwashes.php")
    suspend fun getCarWashes(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<List<CarWashDTO>>

    @FormUrlEncoded
    @POST("/php/app/vehicles/get_position.php")
    suspend fun getCarPosition(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("ver") appVersion: Int
    ): Response<CarPositionDTO>
}