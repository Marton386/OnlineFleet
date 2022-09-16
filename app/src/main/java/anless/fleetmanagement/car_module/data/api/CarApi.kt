package anless.fleetmanagement.car_module.data.api

import anless.fleetmanagement.car_module.data.model.CarDTO
import anless.fleetmanagement.car_module.data.model.CarItemDTO
import anless.fleetmanagement.car_module.data.model.ContractDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CarApi {

    @FormUrlEncoded
    @POST("/php/app/vehicles/find_vehicles.php")
    suspend fun searchCar(
        @Field("hash") hash: String,
        @Field("str") partLicensePlate: String,
        @Field("ver") appVersion: Int
    ): Response<List<CarItemDTO>>

    @FormUrlEncoded
    @POST("/php/app/vehicles/get_vehicle.php")
    suspend fun getCar(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("ver") appVersion: Int
    ): Response<CarDTO>

    @FormUrlEncoded
    @POST("php/app/offers/find_offer.php")
    suspend fun getContract(
        @Field("hash") hash: String,
        @Field("str") contractID: String,
    ): Response<ContractDTO>
}