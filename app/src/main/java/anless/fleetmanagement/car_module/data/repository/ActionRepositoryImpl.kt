package anless.fleetmanagement.car_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.car_module.data.data_source.action.ActionDataSource
import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.car_module.domain.repository.ActionRepository
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class ActionRepositoryImpl(
    private val authorizationManager: AuthorizationManager,
    private val actionDataSource: ActionDataSource
) : ActionRepository {

    override suspend fun pickup(pickup: Action.Pickup): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.pickup(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                pickup = pickup
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun dropOff(dropOff: Action.DropOff): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.dropOff(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                dropOff = dropOff
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun decommissioning(decommissioning: Action.Decommissioning): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.decommissioning(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                decommissioning = decommissioning
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun commissioning(commissioning: Action.Commissioning): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.commissioning(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                commissioning = commissioning
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun relocationStart(startRelocation: Action.Relocation.Start): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.relocationStart(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                startRelocation = startRelocation
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun relocationEnd(endRelocation: Action.Relocation.End): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.relocationEnd(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                endRelocation = endRelocation
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun delivery(delivery: Action.Delivery): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.delivery(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                delivery = delivery
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun maintenanceStart(startMaintenance: Action.Maintenance.Start): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.maintenanceStart(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                startMaintenance = startMaintenance
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun maintenanceEnd(endMaintenance: Action.Maintenance.End): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.maintenanceEnd(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                endMaintenance = endMaintenance
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun changeTires(changeTires: Action.ChangeTires): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.changeTires(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                changeTires = changeTires
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun refillFuel(refillFuel: Action.RefillFuel): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.refillFuel(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                refillFuel = refillFuel
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun washing(washing: Action.Washing): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return actionDataSource.washing(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                washing = washing
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}