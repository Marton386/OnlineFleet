package anless.fleetmanagement.user_module.data.data_source.shift

import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.data.api.ShiftApi
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.OpenShiftInfo
import anless.fleetmanagement.user_module.domain.model.Shift
import anless.fleetmanagement.user_module.domain.model.ShiftInfo
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import retrofit2.HttpException
import java.net.UnknownHostException

class ShiftDataSourceImpl(
    private val shiftApi: ShiftApi
) : ShiftDataSource {
    override suspend fun getCurrentShift(sourceInfo: SourceInfo): Result<Shift> =
        try {
            val response = shiftApi.getCurrentShift(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(response.body()!!.toShift())
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun openShift(openShiftInfo: OpenShiftInfo): Result<Boolean> =
        try {
            val response = shiftApi.openShift(
                hash = openShiftInfo.shiftParam.sourceParam.userHash.hash,
                idStation = openShiftInfo.idStation,
                latitude = openShiftInfo.shiftParam.location.latitude,
                longitude = openShiftInfo.shiftParam.location.longitude,
                notificationToken = "",
                appVersion = openShiftInfo.shiftParam.sourceParam.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun closeShift(shiftInfo: ShiftInfo): Result<Boolean> =
        try {
            val response = shiftApi.closeShift(
                hash = shiftInfo.sourceParam.userHash.hash,
                latitude = shiftInfo.location.latitude,
                longitude = shiftInfo.location.longitude,
                appVersion = shiftInfo.sourceParam.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(ErrorCodes.EXCEPTION)
        }
}