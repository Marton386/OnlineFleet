package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.SimpleItem
import com.google.gson.annotations.SerializedName

data class CarWashDTO(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val name: String
) {
    fun toSimpleItem() = SimpleItem(
        id = id,
        name = name
    )
}
