package anless.fleetmanagement.car_module.domain.usecase.action

import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.car_module.domain.repository.ActionRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class MaintenanceStartUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend operator fun invoke(maintenanceStart: Action.Maintenance.Start): Result<Boolean> {
        return actionRepository.maintenanceStart(maintenanceStart)
    }
}