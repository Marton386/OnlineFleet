package anless.fleetmanagement.car_module.data.data_source.car

import anless.fleetmanagement.car_module.data.api.CarApi
import anless.fleetmanagement.car_module.data.model.ContractDTO
import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import anless.fleetmanagement.car_module.domain.model.LightContract
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.RetrofitParseException
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.net.UnknownHostException

class CarDataSourceImpl(
    private val carApi: CarApi
) : CarDataSource {
    override suspend fun searchCars(
        sourceInfo: SourceInfo,
        partLicensePlate: String
    ): Result<List<CarItem>> =
        try {
            val response = carApi.searchCar(
                hash = sourceInfo.userHash.hash,
                partLicensePlate = partLicensePlate,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val cars = response.body()?.map { carItemDTO ->
                    carItemDTO.toCarItem()
                } ?: throw RetrofitParseException()
                Result.Success(cars)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Search cars exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getCar(sourceInfo: SourceInfo, idCar: Int): Result<Car> =
        try {
            val response = carApi.getCar(
                hash = sourceInfo.userHash.hash,
                idCar = idCar,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                val car = response.body()?.toCar() ?: throw RetrofitParseException()
                Result.Success(car)
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get car exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getContract(
        sourceInfo: SourceInfo,
        contractID: String
    ): Result<LightContract> =
        try {
            val response = carApi.getContract(
                hash = sourceInfo.userHash.hash,
                contractID = contractID
            )

            if (response.isSuccessful) {
                val contract = response.body()?.toLightContract() ?: throw RetrofitParseException()
                Result.Success(contract)
            } else {
                throw HttpException(response)
            }

        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get contract exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }
}