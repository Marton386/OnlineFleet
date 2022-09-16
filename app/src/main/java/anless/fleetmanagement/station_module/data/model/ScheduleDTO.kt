package anless.fleetmanagement.station_module.data.model

import anless.fleetmanagement.car_module.data.utils.Constants
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import com.google.gson.annotations.SerializedName

data class ScheduleDTO(
    @SerializedName("items")
    val schedule: List<ScheduleItemDTO>
) {
    data class ScheduleItemDTO(
        @SerializedName("type")
        val status: Int,

        @SerializedName("id_conf")
        val reservationNumber: String,

        @SerializedName("unix_date")
        val date: Long,

        @SerializedName("station_title")
        val stationTitle: String,

        @SerializedName("vehicle")
        val carModel: String?,

        @SerializedName("gosnomer")
        val carLicensePlate: String?,

        @SerializedName("client")
        val client: String,

        @SerializedName("company")
        val company: String,

        @SerializedName("source")
        val source: String,

        @SerializedName("flight")
        val flightNumber: String,

        @SerializedName("complete")
        val complete: Int
    ) {
        fun toScheduleItem() = ScheduleItem(
            reservation = ScheduleItem.Reservation(
                resNumber = reservationNumber,
                client = client,
                source = ScheduleItem.Reservation.Source(
                    source = source,
                    company = company
                ),
                flightNumber = flightNumber.ifEmpty { null }
            ),
            car = if (carLicensePlate != null) ScheduleItem.Car(
                model = carModel!!,
                licensePlate = carLicensePlate
            ) else null,
            date = date,
            actionType = status,
            stationTitle = stationTitle,
            isComplete = complete == Constants.SCHEDULE_ITEM_IS_COMPLETE
        )
    }

    fun toSchedule() = schedule.map { it.toScheduleItem() }
}
