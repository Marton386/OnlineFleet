package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.Car
import anless.fleetmanagement.car_module.domain.model.CarItem
import com.google.gson.annotations.SerializedName

data class CarItemDTO(

    @SerializedName("id")
    val idVehicle: Int,

    @SerializedName("gosnomer")
    val licensePlate: String,

    @SerializedName("title")
    val model: String,

    @SerializedName("station_id")
    val idStation: Int,

    @SerializedName("station_short_code")
    val codeStation: String,
) {
    fun toCarItem(): CarItem {
        return CarItem(
            carInfo = Car.CarInfo(
                id = idVehicle,
                model = model,
                licensePlate = licensePlate,
            ),
            stationInfo = Car.StationInfo(
                idStation = idStation,
                codeStation = codeStation
            )
        )
    }
}
