package anless.fleetmanagement.user_module.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SessionApi {
    @FormUrlEncoded
    @POST("/php/app/auth/check_app_session.php")
    suspend fun checkSession(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/auth/create_app_session.php")
    suspend fun createSession(
        @Field("login") login: String,
        @Field("pass") password: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/php/app/auth/clear_app_session.php")
    suspend fun clearSession(
        @Field("hash") hash: String,
        @Field("ver") appVersion: Int
    ): Response<ResponseBody> // String for hash?
}