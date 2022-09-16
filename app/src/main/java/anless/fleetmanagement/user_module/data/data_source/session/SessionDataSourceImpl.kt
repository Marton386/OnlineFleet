package anless.fleetmanagement.user_module.data.data_source.session

import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.data.api.SessionApi
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo
import retrofit2.HttpException
import java.net.UnknownHostException

class SessionDataSourceImpl(
    private val sessionApi: SessionApi
) : SessionDataSource {
    override suspend fun checkSession(sourceInfo: SourceInfo): Result<Boolean> =
        try {
            val response = sessionApi.checkSession(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
                //Result.Error(ErrorCodes.NO_INTERNET)
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

    override suspend fun createSession(userLoginInfo: UserLoginInfo): Result<UserHash> =
        try {
            val response = sessionApi.createSession(
                login = userLoginInfo.login,
                password = userLoginInfo.password,
                appVersion = userLoginInfo.appVersion
            )

            if (response.isSuccessful) {
                Result.Success(UserHash(response.body()!!.string()))
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

    override suspend fun clearSession(sourceInfo: SourceInfo): Result<Boolean> =
        try {
            val response = sessionApi.clearSession(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
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