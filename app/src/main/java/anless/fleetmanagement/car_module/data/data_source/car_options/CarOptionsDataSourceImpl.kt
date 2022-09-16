package anless.fleetmanagement.car_module.data.data_source.car_options

import anless.fleetmanagement.car_module.data.api.CarOptionsApi
import anless.fleetmanagement.car_module.domain.model.*
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.RetrofitParseException
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.net.UnknownHostException

class CarOptionsDataSourceImpl(
    private val carOptionsApi: CarOptionsApi
) : CarOptionsDataSource {

    override suspend fun geCurrentTires(
        sourceInfo: SourceInfo,
        idCar: Int
    ): Result<TireParams.Type> =
        try {
            val response = carOptionsApi.getCurrentTires(
                hash = sourceInfo.userHash.hash,
                idCar = idCar,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val tireType = response.body()?.toTireType()
                if (tireType != null) {
                    Result.Success(tireType)
                } else {
                    Result.Error(ErrorCodes.NOT_FOUND)
                }
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get current tires exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getRelocations(sourceInfo: SourceInfo): Result<List<Relocation>> =
        try {
            val response = carOptionsApi.getRelocations(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val relocations =
                    response.body()?.map { it.toRelocation() } ?: throw RetrofitParseException()
                Result.Success(relocations)
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

    override suspend fun getReservations(
        sourceInfo: SourceInfo,
        reservationSearch: ReservationSearch
    ): Result<List<ReservationItem>> =
        try {
            val response = carOptionsApi.getReservations(
                hash = sourceInfo.userHash.hash,
                resNumber = reservationSearch.resNumber,
                idCar = reservationSearch.idCar,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val reservation = response.body()?.map { it.toReservationItem() }
                    ?: throw RetrofitParseException()
                Result.Success(reservation)
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get reservations exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getCarWashes(sourceInfo: SourceInfo): Result<List<SimpleItem>> =
        try {
            val response = carOptionsApi.getCarWashes(
                hash = sourceInfo.userHash.hash,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val items =
                    response.body()?.map { it.toSimpleItem() } ?: throw RetrofitParseException()
                Result.Success(items)
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

    override suspend fun getCarPosition(sourceInfo: SourceInfo, idCar: Int): Result<CarPosition> =
        try {
            val response = carOptionsApi.getCarPosition(
                hash = sourceInfo.userHash.hash,
                idCar = idCar,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val carPosition = response.body()?.toCarPosition() ?: throw RetrofitParseException()
                Result.Success(carPosition)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get car position exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }
}