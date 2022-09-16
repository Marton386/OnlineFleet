package anless.fleetmanagement.car_module.domain.repository

import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.core.utils.data_result.Result

interface ActionRepository {
    suspend fun pickup(pickup: Action.Pickup): Result<Boolean>

    suspend fun dropOff(dropOff: Action.DropOff): Result<Boolean>

    suspend fun decommissioning(decommissioning: Action.Decommissioning): Result<Boolean>

    suspend fun commissioning(commissioning: Action.Commissioning): Result<Boolean>

    suspend fun relocationStart(startRelocation: Action.Relocation.Start): Result<Boolean>

    suspend fun relocationEnd(endRelocation: Action.Relocation.End): Result<Boolean>

    suspend fun delivery(delivery: Action.Delivery): Result<Boolean>

    suspend fun maintenanceStart(startMaintenance: Action.Maintenance.Start): Result<Boolean>

    suspend fun maintenanceEnd(endMaintenance: Action.Maintenance.End): Result<Boolean>

    suspend fun changeTires(changeTires: Action.ChangeTires): Result<Boolean>

    suspend fun refillFuel(refillFuel: Action.RefillFuel): Result<Boolean>

    suspend fun washing(washing: Action.Washing): Result<Boolean>
}