package anless.fleetmanagement.user_module.domain.usecase

import javax.inject.Inject

data class UserUseCases @Inject constructor(
    val checkSessionUseCase: CheckSessionUseCase,
    val createSessionUseCase: CreateSessionUseCase,
    val clearSessionUseCase: ClearSessionUseCase,
    val getUserUseCase: GetUserUseCase,
    val getCurrentShiftUseCase: GetCurrentShiftUseCase,
    val openShiftUseCase: OpenShiftUseCase,
    val closeShiftUseCase: CloseShiftUseCase
)