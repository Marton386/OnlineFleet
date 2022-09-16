package anless.fleetmanagement.car_module.domain.usecase.car_options

import anless.fleetmanagement.car_module.domain.model.CarPosition
import anless.fleetmanagement.car_module.domain.repository.CarOptionsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetCarPositionUseCase @Inject constructor(
    private val carOptionsRepository: CarOptionsRepository
) {
    suspend operator fun invoke(idCar: Int): Result<CarPosition> {
        return carOptionsRepository.getCarPosition(idCar)
    }
}