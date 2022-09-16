package anless.fleetmanagement.car_module.data.data_source.car_params

import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface CarParamsDataSource{
    suspend fun getEquipment(equipmentNumber: String): Result<Equipment>
    suspend fun checkOverprices(idCar: Int, carParam: CarParam): Result<Overprices>
    suspend fun prepareInsurance(
        sourceInfo: SourceInfo,
        filename: String
    ): Result<Boolean>
}