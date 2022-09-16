package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo
import javax.inject.Inject

class CreateSessionUseCase @Inject constructor(
    private val authorizationManager: AuthorizationManager
) {
    suspend operator fun invoke(userLoginInfo: UserLoginInfo): Result<UserHash> {
        return authorizationManager.login(userLoginInfo)
    }
}