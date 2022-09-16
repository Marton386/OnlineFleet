package anless.fleetmanagement.car_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.car_module.data.data_source.car_params.CarParamsDataSource
import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.car_module.domain.repository.CarParamsRepository
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class CarParamsRepositoryImpl(
    private val authorizationManager: AuthorizationManager,
    private val carParamsDataSource: CarParamsDataSource
) : CarParamsRepository {

    override suspend fun getEquipment(equipmentNumber: String): Result<Equipment> {
        return carParamsDataSource.getEquipment(equipmentNumber)
    }

    override suspend fun checkOverprices(idCar: Int, carParam: CarParam): Result<Overprices> {
        return carParamsDataSource.checkOverprices(idCar = idCar, carParam = carParam)
    }

    override suspend fun prepareInsurance(filename: String): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carParamsDataSource.prepareInsurance(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                filename = filename
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}