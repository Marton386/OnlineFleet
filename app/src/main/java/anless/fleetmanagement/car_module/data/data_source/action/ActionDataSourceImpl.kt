package anless.fleetmanagement.car_module.data.data_source.action

import anless.fleetmanagement.car_module.data.api.ActionApi
import anless.fleetmanagement.car_module.data.utils.RepairWorks
import anless.fleetmanagement.car_module.data.utils.TireOptionsManager
import anless.fleetmanagement.car_module.domain.model.Action
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import retrofit2.HttpException
import java.net.UnknownHostException

class ActionDataSourceImpl(
    private val actionApi: ActionApi
) : ActionDataSource {
    override suspend fun pickup(sourceInfo: SourceInfo, pickup: Action.Pickup): Result<Boolean> =
        try {
            val contractDefaultValue = "1"
            val extrasDefaultValue = ""

            val response = actionApi.pickup(
                hash = sourceInfo.userHash.hash,
                idCar = pickup.idCar,
                idStation = pickup.idStation,
                mileage = pickup.carParam.mileage,
                fuel = pickup.carParam.fuel,
                clearState = pickup.carParam.cleanState,
                reservationNumber = pickup.reservation.resNumber,
                idSource = pickup.reservation.idSource,
                extras = extrasDefaultValue,
                actPhotoFileName = pickup.actPhotoFileName,
                contractNumber = contractDefaultValue,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Pickup exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun dropOff(sourceInfo: SourceInfo, dropOff: Action.DropOff): Result<Boolean> =
        try {
            val response = actionApi.drop_off(
                hash = sourceInfo.userHash.hash,
                idCar = dropOff.idCar,
                idStation = dropOff.idStation,
                mileage = dropOff.carParam.mileage,
                fuel = dropOff.carParam.fuel,
                clearState = dropOff.carParam.cleanState,
                actPhotoFileName = dropOff.actPhotoFileName,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Drop off exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun decommissioning(
        sourceInfo: SourceInfo,
        decommissioning: Action.Decommissioning
    ): Result<Boolean> =
        try {
            val response = actionApi.decommissioning(
                hash = sourceInfo.userHash.hash,
                idCar = decommissioning.idCar,
                idStation = decommissioning.idStation,
                daysStop = decommissioning.daysStop,
                comment = decommissioning.comment,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                throw  HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Decommissioning exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun commissioning(
        sourceInfo: SourceInfo,
        commissioning: Action.Commissioning
    ): Result<Boolean> =
        try {
            val response = actionApi.commissioning(
                hash = sourceInfo.userHash.hash,
                idCar = commissioning.idCar,
                licensePlate = commissioning.licensePlate,
                idStation = commissioning.idStation,
                mileage = commissioning.carParam.mileage,
                fuel = commissioning.carParam.fuel,
                clearState = commissioning.carParam.cleanState,
                comment = commissioning.comment,
                appVersion = sourceInfo.appVersionCode
            )

            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                throw  HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Commissioning exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun relocationStart(
        sourceInfo: SourceInfo,
        startRelocation: Action.Relocation.Start
    ): Result<Boolean> =
        try {
            val response = actionApi.startRelocation(
                hash = sourceInfo.userHash.hash,
                idCar = startRelocation.idCar,
                idStation = startRelocation.idStation,
                comment = startRelocation.comment,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Start relocation exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun relocationEnd(
        sourceInfo: SourceInfo,
        endRelocation: Action.Relocation.End
    ): Result<Boolean> =
        try {
            val response = actionApi.endRelocation(
                hash = sourceInfo.userHash.hash,
                idCar = endRelocation.idCar,
                idStation = endRelocation.idStation,
                mileage = endRelocation.carParam.mileage,
                fuel = endRelocation.carParam.fuel,
                clearState = endRelocation.carParam.cleanState,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "End relocation exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun delivery(
        sourceInfo: SourceInfo,
        delivery: Action.Delivery
    ): Result<Boolean> =
        try {
            val response = actionApi.delivery(
                hash = sourceInfo.userHash.hash,
                idCar = delivery.idCar,
                idStation = delivery.idStation,
                reservationNumber = delivery.reservation.resNumber,
                idSource = delivery.reservation.idSource,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Delivery exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun maintenanceStart(
        sourceInfo: SourceInfo,
        startMaintenance: Action.Maintenance.Start
    ): Result<Boolean> =
        try {
            val defaultValueTO = 0
            val defaultPhotoFilenames = ""

            val response = actionApi.startMaintenance(
                hash = sourceInfo.userHash.hash,
                idCar = startMaintenance.idCar,
                idStation = startMaintenance.idStation,
                mileage = startMaintenance.mileage,
                typeRepair = RepairWorks.getWorksType(startMaintenance.params.typeRepair),
                typeTO = if (startMaintenance.params.typeTO != null)
                    RepairWorks.getMaintenanceType(
                        startMaintenance.params.typeTO
                    ) else defaultValueTO,
                contractor = startMaintenance.contractor,
                comment = startMaintenance.comment,
                repairPhotoFilename = startMaintenance.params.repairPhotosFilename
                    ?: defaultPhotoFilenames,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Start maintenance exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun maintenanceEnd(
        sourceInfo: SourceInfo,
        endMaintenance: Action.Maintenance.End
    ): Result<Boolean> =
        try {
            val response = actionApi.endMaintenance(
                hash = sourceInfo.userHash.hash,
                idCar = endMaintenance.idCar,
                idStation = endMaintenance.idStation,
                mileage = endMaintenance.mileage,
                invoiceNumber = endMaintenance.invoiceNumber,
                price = endMaintenance.price,
                comment = endMaintenance.comment,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "End maintenance exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }


    override suspend fun changeTires(
        sourceInfo: SourceInfo,
        changeTires: Action.ChangeTires
    ): Result<Boolean> =
        try {
            val response = actionApi.changeTires(
                hash = sourceInfo.userHash.hash,
                idCar = changeTires.idCar,
                season = TireOptionsManager.getTypeInt(changeTires.tireParams.type),
                width = changeTires.tireParams.width,
                profile = changeTires.tireParams.profile,
                diameter = changeTires.tireParams.diameter,
                condition = TireOptionsManager.getConditionInt(changeTires.tireParams.condition),
                price = changeTires.price,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Change tires exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun refillFuel(
        sourceInfo: SourceInfo,
        refillFuel: Action.RefillFuel
    ): Result<Boolean> =
        try {
            val response = actionApi.refillFuel(
                hash = sourceInfo.userHash.hash,
                idCar = refillFuel.idCar,
                litres = refillFuel.litres,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Refill fuel exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun washing(sourceInfo: SourceInfo, washing: Action.Washing): Result<Boolean> =
        try {
            val response = actionApi.washing(
                hash = sourceInfo.userHash.hash,
                idCar = washing.idCar,
                idCarWash = washing.idCarWash,
                price = washing.price,
                appVersion = sourceInfo.appVersionCode
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
            Firebase.crashlytics.log(e.message ?: "Washing exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }
}