package anless.fleetmanagement.car_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.car_module.data.data_source.car_options.CarOptionsDataSource
import anless.fleetmanagement.car_module.domain.model.*
import anless.fleetmanagement.car_module.domain.repository.CarOptionsRepository
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class CarOptionsRepositoryImpl(
    private val authorizationManager: AuthorizationManager,
    private val carOptionsDataSource: CarOptionsDataSource
) : CarOptionsRepository {

    override suspend fun getCurrentTires(idCar: Int): Result<TireParams.Type> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carOptionsDataSource.geCurrentTires(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                idCar = idCar
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getRelocations(): Result<List<Relocation>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carOptionsDataSource.getRelocations(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getReservations(reservationSearch: ReservationSearch): Result<List<ReservationItem>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carOptionsDataSource.getReservations(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                reservationSearch = reservationSearch
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getCarWashes(): Result<List<SimpleItem>> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carOptionsDataSource.getCarWashes(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun getCarPosition(idCar: Int): Result<CarPosition> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return carOptionsDataSource.getCarPosition(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                idCar = idCar
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}