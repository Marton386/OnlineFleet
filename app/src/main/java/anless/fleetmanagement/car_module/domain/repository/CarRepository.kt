package anless.fleetmanagement.car_module.domain.repository

import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.core.utils.data_result.Result


interface CarRepository {
    suspend fun searchCar(partLicensePlate: String): Result<List<CarItem>>

    suspend fun getCar(idCar: Int): Result<Car>

    suspend fun getContract(contractID: String): Result<LightContract>
}