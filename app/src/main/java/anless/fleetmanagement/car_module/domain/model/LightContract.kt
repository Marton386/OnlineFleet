package anless.fleetmanagement.car_module.domain.model

data class LightContract(
    val refid: String,
    val vehicleID: Long,
    val status: Long,
    val client: Client
) {
    data class Client (
        val id: Long,
        val fullTitle: String
    )
}