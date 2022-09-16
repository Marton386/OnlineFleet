package anless.fleetmanagement.car_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.car_module.data.data_source.car.CarDataSource
import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.car_module.domain.repository.CarRepository
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class CarRepositoryImpl(
    private val authorizationManager: AuthorizationManager,
    private val carDataSource: CarDataSource
) : CarRepository {
    override suspend fun searchCar(partLicensePlate: String): Result<List<CarItem>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carDataSource.searchCars(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                partLicensePlate = partLicensePlate
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getCar(idCar: Int): Result<Car> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carDataSource.getCar(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                idCar = idCar
            )
        }
        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getContract(contractID: String): Result<LightContract> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carDataSource.getContract(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                contractID = contractID
            )
        }
        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}