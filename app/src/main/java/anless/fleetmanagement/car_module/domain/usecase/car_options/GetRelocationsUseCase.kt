package anless.fleetmanagement.car_module.domain.usecase.car_options

import anless.fleetmanagement.car_module.domain.model.Relocation
import anless.fleetmanagement.car_module.domain.repository.CarOptionsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetRelocationsUseCase @Inject constructor(
    private val carOptionsRepository: CarOptionsRepository
) {
    suspend operator fun invoke(): Result<List<Relocation>> {
        return carOptionsRepository.getRelocations()
    }
}