package anless.fleetmanagement.user_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.data.data_source.shift.ShiftDataSource
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.*
import anless.fleetmanagement.user_module.domain.repository.ShiftRepository
import com.google.android.gms.maps.model.LatLng

class ShiftRepositoryImpl(
    private val shiftDataSource: ShiftDataSource,
    private val authorizationManager: AuthorizationManager
) : ShiftRepository {
    override suspend fun getCurrentShift(): Result<Shift> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return shiftDataSource.getCurrentShift(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun openShift(shiftLocation: ShiftLocation): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return shiftDataSource.openShift(
                openShiftInfo = OpenShiftInfo(
                    shiftParam = ShiftInfo(
                        sourceParam = SourceInfo(
                            userHash = UserHash(hash),
                            appVersionCode = BuildConfig.VERSION_CODE
                        ),
                        location = shiftLocation.locationLatLng ?: LatLng(0.0,0.0)
                    ),
                    idStation = shiftLocation.idStation ?: 0
                )
            )
        }
        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun closeShift(location: LatLng): Result<Boolean> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return shiftDataSource.closeShift(
                shiftInfo = ShiftInfo(
                    sourceParam = SourceInfo(
                        userHash = UserHash(hash),
                        appVersionCode = BuildConfig.VERSION_CODE
                    ),
                    location = location
                )
            )
        }
        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }
}