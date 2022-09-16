package anless.fleetmanagement.user_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.data.data_source.user.UserDataSource
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.User
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val authorizationManager: AuthorizationManager
) : UserRepository {
/*    override suspend fun getUser(sourceInfo: SourceInfo): Result<User> {
        return userDataSource.getUser(sourceInfo)
    }*/

    override suspend fun getUser(): Result<User> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return userDataSource.getUser(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}