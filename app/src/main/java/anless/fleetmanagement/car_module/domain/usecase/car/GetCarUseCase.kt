package anless.fleetmanagement.car_module.domain.usecase.car

import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.repository.CarRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetCarUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    suspend operator fun invoke(idCar: Int): Result<Car> {
        return carRepository.getCar(idCar)
    }
}