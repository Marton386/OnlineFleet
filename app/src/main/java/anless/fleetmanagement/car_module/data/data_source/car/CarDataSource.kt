package anless.fleetmanagement.car_module.data.data_source.car

import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface CarDataSource {
    suspend fun searchCars(sourceInfo: SourceInfo, partLicensePlate: String): Result<List<CarItem>>

    suspend fun getCar(sourceInfo: SourceInfo, idCar: Int): Result<Car>

    suspend fun getContract(sourceInfo: SourceInfo, contractID: String): Result<LightContract>
}