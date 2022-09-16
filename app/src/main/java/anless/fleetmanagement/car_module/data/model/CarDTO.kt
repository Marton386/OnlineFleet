package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.data.utils.CleanState
import anless.fleetmanagement.car_module.data.utils.Constants
import anless.fleetmanagement.car_module.domain.model.Car
import com.google.gson.annotations.SerializedName

data class CarDTO(
    @SerializedName("id")
    val idVehicle: Int,

    @SerializedName("gosnomer")
    val licensePlate: String,

    @SerializedName("title")
    val model: String,

    @SerializedName("station_id")
    val idStation: Int,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("mileage")
    val mileage: Int,

    @SerializedName("last_to_mileage")
    val mileageLastTO: Int,

    @SerializedName("station_short_code")
    val codeStation: String,

    @SerializedName("clean")
    val clearState: Int,

    @SerializedName("state")
    val state: Int,

    @SerializedName("corp")
    val corporate: Int,

    @SerializedName("wait_to")
    val waitingCheckTO: Int,

    @SerializedName("acriss_obj")
    val carInfo: CarInfoDTO,

    @SerializedName("ins_obj")
    val insurance: InsuranceDTO?,

    @SerializedName("history_arr")
    val historyActions: List<HistoryActionDTO>
) {
    data class CarInfoDTO(
        @SerializedName("code")
        val code: String
    )

    data class InsuranceDTO(
        @SerializedName("to_date")
        val dateTo: Long,

        @SerializedName("file")
        val file: FileDTO?
    ) {
        data class FileDTO(
            @SerializedName("name")
            val name: String
        )
    }

    fun toCar(): Car {
        return Car(
            carInfo = Car.CarInfo(
                id = idVehicle,
                model = model,
                licensePlate = licensePlate,
            ),
            code = carInfo.code,
            options = Car.Options(
                mileage = mileage,
                isClean = clearState == CleanState.CLEAN,
                maintenance = Car.Options.TO(
                    mileage = mileageLastTO,
                    waitingCheck = waitingCheckTO == Constants.WAITING_TO
                )
            ),
            stationInfo = Car.StationInfo(
                idStation = idStation,
                codeStation = codeStation,
            ),
            owner = owner,
            isCorporate = corporate == Constants.CORPORATE_CAR,
            status = state,
            //insuranceFilename = insurance?.file?.name,
            historyActions = historyActions.map { it.toActionHistoryItem() },
            insurance = if (insurance != null) Car.Insurance(
                filename = insurance.file?.name,
                expireDate = insurance.dateTo
            ) else null
        )
    }
}

