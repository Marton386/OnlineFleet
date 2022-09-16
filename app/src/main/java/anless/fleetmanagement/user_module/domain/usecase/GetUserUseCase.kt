package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.User
import anless.fleetmanagement.user_module.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
/*    suspend operator fun invoke(sourceInfo: SourceInfo): Result<User> {
        return userRepository.getUser(sourceInfo)
    }*/

    suspend operator fun invoke(): Result<User> {
        return userRepository.getUser()
    }
}