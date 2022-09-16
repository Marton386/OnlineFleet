package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.data.utils.EquipmentTypes
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.utils.OptionalEquipment
import com.google.gson.annotations.SerializedName

data class EquipmentDTO(
    @SerializedName("type")
    val type: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("code")
    val code: String
) {
    fun toEquipment() = Equipment(
        type = when(type) {
            EquipmentTypes.GPS_NAVIGATOR -> OptionalEquipment.GPS_NAVIGATOR
            EquipmentTypes.CHILD_SEAT -> OptionalEquipment.CHILD_SEAT
            else -> OptionalEquipment.GPS_NAVIGATOR
        },
        code = code,
        description = description
    )
}
