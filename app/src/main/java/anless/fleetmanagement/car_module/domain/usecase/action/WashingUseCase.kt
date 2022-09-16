package anless.fleetmanagement.car_module.domain.usecase.action

import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.car_module.domain.repository.ActionRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class WashingUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend operator fun invoke(washing: Action.Washing): Result<Boolean> {
        return actionRepository.washing(washing)
    }
}