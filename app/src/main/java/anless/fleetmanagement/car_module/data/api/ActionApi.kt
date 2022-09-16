package anless.fleetmanagement.car_module.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ActionApi {

    @FormUrlEncoded
    @POST("/php/app/vehicles/pickup.php")
    suspend fun pickup(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("fuel") fuel: Int,
        @Field("clear") clearState: Int,
        @Field("refid") reservationNumber: String,
        @Field("source_id") idSource: Int,
        @Field("extras") extras: String,
        @Field("photos") actPhotoFileName: String,
        @Field("aprok") contractNumber: String, // send Constants.APROK_DEFAULT_VALUE
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/dropoff.php")
    suspend fun drop_off(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("fuel") fuel: Int,
        @Field("clear") clearState: Int,
        @Field("photos") actPhotoFileName: String,
        @Field("ver") appVersion: Int
        ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/decommissioning.php")
    suspend fun decommissioning(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("stopdays") daysStop: Int,
        @Field("comment") comment: String,
        @Field("ver") appVersion: Int
    ):Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/commissioning.php")
    suspend fun commissioning(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("gosnomer") licensePlate: String,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("fuel") fuel: Int,
        @Field("clear") clearState: Int,
        @Field("comment") comment: String, // not require
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/relocation_start.php")
    suspend fun startRelocation(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("comment") comment: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/relocation_end.php")
    suspend fun endRelocation(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("fuel") fuel: Int,
        @Field("clear") clearState: Int,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>


    @FormUrlEncoded
    @POST("/php/app/vehicles/delivery2.php")
    suspend fun delivery(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("res_id") reservationNumber: String,
        @Field("source_id") idSource: Int,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/maintenance_start.php")
    suspend fun startMaintenance(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("rt") typeRepair: Int,
        @Field("rc") contractor: String,
        @Field("t_o") typeTO: Int,
        @Field("comment") comment: String,
        @Field("photos") repairPhotoFilename: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/maintenance_end.php")
    suspend fun endMaintenance(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("station_id") idStation: Int,
        @Field("mileage") mileage: Int,
        @Field("nakladn") invoiceNumber: String,
        @Field("price2") price: Int,
        @Field("comment") comment: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/change_tires.php")
    suspend fun changeTires(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("wt") season: Int,
        @Field("tt1") width: Int,
        @Field("tt2") profile: Int,
        @Field("tt3") diameter: Int,
        @Field("tstatus") condition: Int,
        @Field("price") price: Int,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/refill_fuel.php")
    suspend fun refillFuel(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("liters") litres: Int,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/vehicles/carwash.php")
    suspend fun washing(
        @Field("hash") hash: String,
        @Field("vehicle_id") idCar: Int,
        @Field("carwash_id") idCarWash: Int,
        @Field("carwash_price") price: Int,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>
}