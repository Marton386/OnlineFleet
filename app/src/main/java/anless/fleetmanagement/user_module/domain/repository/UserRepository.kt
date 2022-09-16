package anless.fleetmanagement.user_module.domain.repository

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.*

interface UserRepository {
    /*suspend fun getUser(sourceInfo: SourceInfo): Result<User>*/
    suspend fun getUser(): Result<User>
}