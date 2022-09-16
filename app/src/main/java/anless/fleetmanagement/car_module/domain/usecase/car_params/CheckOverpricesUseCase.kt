package anless.fleetmanagement.car_module.domain.usecase.car_params

import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.car_module.domain.repository.CarParamsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class CheckOverpricesUseCase @Inject constructor(
    private val carParamsRepository: CarParamsRepository
) {
    suspend operator fun invoke(idCar: Int, carParam: CarParam): Result<Overprices> {
        return carParamsRepository.checkOverprices(idCar = idCar, carParam = carParam)
    }
}