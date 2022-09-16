package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.CarPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class CarPositionDTO(
    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("last_update")
    val lastUpdate: String,

    @SerializedName("timezone")
    val timezone: String,

    @SerializedName("speed")
    val speed: Double,

    @SerializedName("acc")
    val voltage: Double
) {
    fun toCarPosition() = CarPosition(
        lastUpdate = getParsedDate(),
        speed = speed,
        voltage = voltage,
        location = LatLng(latitude, longitude)
    )

    private fun getParsedDate(): Long? {
        val locale = Locale.US
        val format = "yyyy-MM-dd HH:mm"
        val timezone = TimeZone.getTimeZone("UTC")
        val formatter = SimpleDateFormat(format, locale)
        formatter.timeZone = timezone

        val calendar = Calendar.getInstance(timezone)
        val date = formatter.parse(this.lastUpdate)

        if (date != null) {
            calendar.time = date
            return calendar.timeInMillis / 1000
        }

        return null
    }
}
