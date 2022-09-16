package anless.fleetmanagement.car_module.domain.repository

import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.core.utils.data_result.Result

interface CarParamsRepository {

    suspend fun getEquipment(equipmentNumber: String): Result<Equipment>
    suspend fun checkOverprices(idCar: Int, carParam: CarParam): Result<Overprices>
    suspend fun prepareInsurance(filename: String): Result<Boolean>

}