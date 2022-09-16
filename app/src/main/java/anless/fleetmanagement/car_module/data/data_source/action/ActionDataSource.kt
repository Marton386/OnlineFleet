package anless.fleetmanagement.car_module.data.data_source.action

import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface ActionDataSource {
    suspend fun pickup(sourceInfo: SourceInfo, pickup: Action.Pickup): Result<Boolean>

    suspend fun dropOff(sourceInfo: SourceInfo, dropOff: Action.DropOff): Result<Boolean>

    suspend fun decommissioning(sourceInfo: SourceInfo, decommissioning: Action.Decommissioning): Result<Boolean>

    suspend fun commissioning(sourceInfo: SourceInfo, commissioning: Action.Commissioning): Result<Boolean>

    suspend fun relocationStart(sourceInfo: SourceInfo, startRelocation: Action.Relocation.Start): Result<Boolean>

    suspend fun relocationEnd(sourceInfo: SourceInfo, endRelocation: Action.Relocation.End): Result<Boolean>

    suspend fun delivery(sourceInfo: SourceInfo, delivery: Action.Delivery): Result<Boolean>

    suspend fun maintenanceStart(sourceInfo: SourceInfo, startMaintenance: Action.Maintenance.Start): Result<Boolean>

    suspend fun maintenanceEnd(sourceInfo: SourceInfo, endMaintenance: Action.Maintenance.End): Result<Boolean>

    suspend fun changeTires(sourceInfo: SourceInfo, changeTires: Action.ChangeTires): Result<Boolean>

    suspend fun refillFuel(sourceInfo: SourceInfo, refillFuel: Action.RefillFuel): Result<Boolean>

    suspend fun washing(sourceInfo: SourceInfo, washing: Action.Washing): Result<Boolean>
}