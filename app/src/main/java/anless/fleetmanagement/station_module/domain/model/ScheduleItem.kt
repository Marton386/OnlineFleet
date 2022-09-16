package anless.fleetmanagement.station_module.domain.model

data class ScheduleItem(
    val actionType: Int,
    val reservation: Reservation,
    val car: Car?,
    val date: Long,
    val stationTitle: String,
    val isComplete: Boolean
) {
    data class Reservation(
        val resNumber: String,
        val client: String,
        val source: Source,
        val flightNumber: String?
    ) {
        data class Source(
            val company: String,
            val source: String
        )
    }

    data class Car(
        val model: String,
        val licensePlate: String
    )
}
