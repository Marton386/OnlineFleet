package anless.fleetmanagement.car_module.domain.model

data class ReservationItem(
    val resNumber: String,
    val clientName: String,
    val company: String,
    val source: Source,
    val error: Int?
) {
    data class Source(
        val id: Int,
        val name: String
    )
}
