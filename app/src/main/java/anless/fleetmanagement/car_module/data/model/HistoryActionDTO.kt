package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.ActionHistoryItem
import com.google.gson.annotations.SerializedName

data class HistoryActionDTO(
    @SerializedName("status")
    val status: Int,

    @SerializedName("name1")
    val managerLastname: String,

    @SerializedName("name2")
    val managerFirstName: String,

    @SerializedName("name3")
    val managerPatronymic: String,

    @SerializedName("createdat")
    val createDate: Long,

    @SerializedName("station_short_code")
    val stationShortCode: String
) {
    fun toActionHistoryItem(): ActionHistoryItem {
        return ActionHistoryItem(
            status = status,
            managerName = "$managerLastname $managerFirstName $managerPatronymic",
            createDate = createDate,
            stationShortCode = stationShortCode
        )
    }
}
