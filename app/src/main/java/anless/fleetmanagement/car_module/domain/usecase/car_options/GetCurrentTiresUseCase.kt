package anless.fleetmanagement.car_module.domain.usecase.car_options

import anless.fleetmanagement.car_module.domain.model.TireParams
import anless.fleetmanagement.car_module.domain.repository.CarOptionsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetCurrentTiresUseCase @Inject constructor(
    private val carOptionsRepository: CarOptionsRepository
) {
    suspend operator fun invoke(idCar: Int): Result<TireParams.Type> {
        return carOptionsRepository.getCurrentTires(idCar)
    }
}