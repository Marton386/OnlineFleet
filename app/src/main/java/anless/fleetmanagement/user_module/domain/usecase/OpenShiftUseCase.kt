package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.ShiftLocation
import anless.fleetmanagement.user_module.domain.repository.ShiftRepository
import javax.inject.Inject

class OpenShiftUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {
    suspend operator fun invoke(shiftLocation: ShiftLocation): Result<Boolean> {
        return shiftRepository.openShift(shiftLocation)
    }
}