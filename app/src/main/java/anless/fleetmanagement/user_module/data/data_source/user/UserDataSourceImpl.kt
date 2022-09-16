package anless.fleetmanagement.user_module.data.data_source.user

import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.data.api.UserApi
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.User
import retrofit2.HttpException
import java.net.UnknownHostException

class UserDataSourceImpl(
    private val userApi: UserApi
) : UserDataSource {
    override suspend fun getUser(sourceInfo: SourceInfo): Result<User> =
        try {
            val response = userApi.getUser(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toUser())
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