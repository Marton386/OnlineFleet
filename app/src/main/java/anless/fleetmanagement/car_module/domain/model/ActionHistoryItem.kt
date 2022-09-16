package anless.fleetmanagement.car_module.domain.model

data class ActionHistoryItem(
    val status: Int,
    val managerName: String,
    val createDate: Long,
    val stationShortCode: String // StationInfo?
)