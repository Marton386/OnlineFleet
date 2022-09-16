package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val authorizationManager: AuthorizationManager
) {
    suspend operator fun invoke(): Result<Boolean> {
        return authorizationManager.checkSession()
    }
}