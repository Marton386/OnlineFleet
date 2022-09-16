package anless.fleetmanagement.car_module.data.api

import anless.fleetmanagement.car_module.data.model.MinimalVersionApp
import anless.fleetmanagement.car_module.data.model.SavedActPhotoInfoDTO
import anless.fleetmanagement.car_module.data.model.SavedMaintenancePhotosInfoDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface DocumentsApi {

    @Multipart
    @POST("/php/app/book/upload_act.php")
    suspend fun saveRentAct(
        @Part("hash") hash: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("ver") appVersion: RequestBody
    ): Response<SavedActPhotoInfoDTO>

    @Multipart
    @POST("/php/app/vehicles/upload_damage_img.php")
    suspend fun saveMaintenancePhotos(
        @Part("hash") hash: RequestBody,
        @Part images: List<MultipartBody.Part>,
        @Part("ver") appVersion: RequestBody
    ): Response<SavedMaintenancePhotosInfoDTO>

    @GET
    suspend fun downloadInsurance(
        @Url url: String
    ): Response<ResponseBody>

    @GET("/php/app/config/server_config.php")
    suspend fun getMinimalVersionApp(
    ): Response<MinimalVersionApp>
}