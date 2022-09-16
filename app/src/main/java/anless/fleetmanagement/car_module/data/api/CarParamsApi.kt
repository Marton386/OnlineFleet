package anless.fleetmanagement.car_module.data.api

import anless.fleetmanagement.car_module.data.model.EquipmentDTO
import anless.fleetmanagement.car_module.data.model.InsuranceFilename
import anless.fleetmanagement.car_module.data.model.OverpricesDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CarParamsApi {

    @GET("/appconfig/rmadmin/php/checkreturncar.php")
    suspend fun checkOverprices(
        @Query("fleet_id") idFleet: Int,
        @Query("mileage") mileage: Int,
        @Query("fuel") fuel: Int,
        @Query("clean") cleanState: Int
    ): Response<OverpricesDTO>

    @GET("/appconfig/rmadmin/php/getextrabycode.php")
    suspend fun getEquipment(
        @Query("code") code: String
    ): Response<EquipmentDTO>

    @POST("/appconfig/rmadmin/php/fleet/get_fleet_ins_doc.php")
    suspend fun prepareInsurance(
        @Body body: InsuranceFilename
    ): Response<ResponseBody>
}