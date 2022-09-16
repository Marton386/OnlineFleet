package anless.fleetmanagement.car_module.domain.usecase.car

import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.repository.CarRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class SearchCarUseCase @Inject constructor(
    private val carRepository: CarRepository
) {

    suspend operator fun invoke(partLicensePlate: String): Result<List<CarItem>> {
        return carRepository.searchCar(partLicensePlate)
    }
}