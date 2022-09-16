package anless.fleetmanagement.car_module.domain.model

data class ActionCarItem(
    val status: Int,
    val date: Long,
    val managerName: String,
    val stationCode: String
)
