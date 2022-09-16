package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.Shift
import anless.fleetmanagement.user_module.domain.repository.ShiftRepository
import javax.inject.Inject

class GetCurrentShiftUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository,
) {
    suspend operator fun invoke(): Result<Shift> {
        return shiftRepository.getCurrentShift()
    }
}