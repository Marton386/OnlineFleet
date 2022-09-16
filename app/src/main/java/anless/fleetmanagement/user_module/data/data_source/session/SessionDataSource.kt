package anless.fleetmanagement.user_module.data.data_source.session

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo

interface SessionDataSource {
    suspend fun checkSession(sourceInfo: SourceInfo): Result<Boolean>

    suspend fun createSession(userLoginInfo: UserLoginInfo): Result<UserHash>

    suspend fun clearSession(sourceInfo: SourceInfo): Result<Boolean>
}