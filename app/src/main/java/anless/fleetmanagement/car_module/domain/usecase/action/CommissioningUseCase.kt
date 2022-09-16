package anless.fleetmanagement.car_module.domain.usecase.action

import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.car_module.domain.repository.ActionRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class CommissioningUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend operator fun invoke(commissioning: Action.Commissioning): Result<Boolean> {
        return actionRepository.commissioning(commissioning)
    }
}