package anless.fleetmanagement.station_module.data.model

import anless.fleetmanagement.station_module.domain.model.Station
import com.google.gson.annotations.SerializedName

data class StationDTO(
    @SerializedName("id")
    val id: Int,

    @SerializedName("city")
    val cityEn: String,

    @SerializedName("name")
    val nameEn: String,

    @SerializedName("city_ru")
    val cityRu: String,

    @SerializedName("name_ru")
    val nameRu: String,

    @SerializedName("short_code")
    val shortCode: String
) {
    fun toStation(): Station {
        return Station(
            id = id,
            nameRu = "$cityRu, $nameRu",
            nameEn = "$cityEn, $nameEn",
            shortCode = shortCode
        )
    }
}
