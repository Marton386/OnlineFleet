package anless.fleetmanagement.user_module.data.data_source.user

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.User

interface UserDataSource {
    suspend fun getUser(sourceInfo: SourceInfo): Result<User>
}