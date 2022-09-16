package anless.fleetmanagement.car_module.domain.usecase.car_params

import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.repository.CarParamsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetEquipmentUseCase @Inject constructor(
    private val carParamsRepository: CarParamsRepository
) {
    suspend operator fun invoke(equipmentNumber: String): Result<Equipment> {
        return carParamsRepository.getEquipment(equipmentNumber)
    }
}