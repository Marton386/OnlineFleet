package anless.fleetmanagement.car_module.domain.model

data class Car(
    val carInfo: CarInfo,
    val code: String,
    val options: Options,
    val stationInfo: StationInfo,
    val owner: String,
    val insurance: Insurance?,
    val isCorporate: Boolean,
    val status: Int,
    val historyActions: List<ActionHistoryItem>
) {
    data class CarInfo(
        val id: Int,
        val model: String,
        val licensePlate: String
    )

    data class StationInfo(
        val idStation: Int,
        val codeStation: String,
    )

    data class Options(
        val mileage: Int,
        val isClean: Boolean,
        val maintenance: TO
    ) {
        data class TO(
            val waitingCheck: Boolean,
            val mileage: Int
            )
    }

    data class Insurance(
        val filename: String?,
        val expireDate: Long
    )
}
