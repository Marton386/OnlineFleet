package anless.fleetmanagement.user_module.data.api

import anless.fleetmanagement.user_module.data.model.UserDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApi {
    @FormUrlEncoded
    @POST("/php/app/user/user.php")
    suspend fun getUser(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<UserDTO>
}