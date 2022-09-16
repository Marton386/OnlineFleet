package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.Relocation
import com.google.gson.annotations.SerializedName

data class RelocationDTO(
    @SerializedName("title")
    val carModel: String,

    @SerializedName("fleet_id")
    val idCar: Int,

    @SerializedName("gosnomer")
    val licensePlate: String,

    @SerializedName("station_short_code")
    val stationShortCode: String
) {
    fun toRelocation(): Relocation = Relocation(
        idCar = idCar,
        carModel = carModel,
        licensePlate = licensePlate,
        stationShortCode = stationShortCode
    )
}
