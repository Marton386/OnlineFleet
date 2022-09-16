package anless.fleetmanagement.user_module.data.data_source.shift

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.OpenShiftInfo
import anless.fleetmanagement.user_module.domain.model.Shift
import anless.fleetmanagement.user_module.domain.model.ShiftInfo
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface ShiftDataSource {
    suspend fun getCurrentShift(sourceInfo: SourceInfo): Result<Shift>

    suspend fun openShift(openShiftInfo: OpenShiftInfo): Result<Boolean>

    suspend fun closeShift(shiftInfo: ShiftInfo): Result<Boolean>
}