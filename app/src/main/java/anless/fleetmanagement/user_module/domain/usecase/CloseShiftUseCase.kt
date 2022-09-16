package anless.fleetmanagement.user_module.domain.usecase

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.repository.ShiftRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class CloseShiftUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {
    suspend operator fun invoke(location: LatLng): Result<Boolean> {
        return shiftRepository.closeShift(location)
    }
}