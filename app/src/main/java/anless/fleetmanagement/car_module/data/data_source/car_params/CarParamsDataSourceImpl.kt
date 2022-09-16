package anless.fleetmanagement.car_module.data.data_source.car_params

import anless.fleetmanagement.car_module.data.api.CarParamsApi
import anless.fleetmanagement.car_module.data.model.InsuranceFilename
import anless.fleetmanagement.car_module.domain.model.CarParam
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.model.Overprices
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.RetrofitParseException
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.net.UnknownHostException

class CarParamsDataSourceImpl(
    private val carParamsApi: CarParamsApi
) : CarParamsDataSource {
    override suspend fun getEquipment(equipmentNumber: String): Result<Equipment> =
        try {
            val response = carParamsApi.getEquipment(
                code = equipmentNumber
            )

            if (response.isSuccessful) {
                val equipment = response.body()?.toEquipment() ?: throw RetrofitParseException()
                Result.Success(equipment)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get equipment exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun checkOverprices(idCar: Int, carParam: CarParam): Result<Overprices> =
        try {
            val response = carParamsApi.checkOverprices(
                idFleet = idCar,
                mileage = carParam.mileage,
                fuel = carParam.fuel,
                cleanState = carParam.cleanState
            )

            if (response.isSuccessful) {
                val overprices = response.body()?.toOverprices() ?: throw RetrofitParseException()
                Result.Success(overprices)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Check overprices exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }


    override suspend fun prepareInsurance(
        sourceInfo: SourceInfo,
        filename: String
    ): Result<Boolean> =
        try {
            val response = carParamsApi.prepareInsurance(
                InsuranceFilename(
                    filename = filename
                )
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
            Firebase.crashlytics.log(e.message ?: "Prepare insurance exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

}