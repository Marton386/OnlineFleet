package anless.fleetmanagement.car_module.domain.model


data class Relocation(
    val idCar: Int,
    val carModel: String,
    val licensePlate: String,
    val stationShortCode: String
)
