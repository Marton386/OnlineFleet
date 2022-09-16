package anless.fleetmanagement.user_module.data.model

import anless.fleetmanagement.station_module.data.model.StationDTO
import anless.fleetmanagement.user_module.domain.model.Shift
import com.google.gson.annotations.SerializedName

data class ShiftDTO(
    @SerializedName("start_time")
    val startTime: Long,

    @SerializedName("station")
    val station: StationDTO
) {
    fun toShift(): Shift {
        return Shift(
            startTime = startTime,
            station = station.toStation()
        )
    }
}
