package anless.fleetmanagement.car_module.domain.usecase.car

import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.car_module.domain.repository.CarRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetContractUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    suspend operator fun invoke(contractID: String): Result<LightContract> {
        return carRepository.getContract(contractID)
    }
}