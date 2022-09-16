package anless.fleetmanagement.user_module.domain.repository

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.*
import com.google.android.gms.maps.model.LatLng

interface ShiftRepository {
    suspend fun getCurrentShift(): Result<Shift>

    suspend fun openShift(shiftLocation: ShiftLocation): Result<Boolean>

    suspend fun closeShift(location: LatLng): Result<Boolean>
}