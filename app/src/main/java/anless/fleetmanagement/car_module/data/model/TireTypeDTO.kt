package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.data.utils.TireOptionsManager
import anless.fleetmanagement.car_module.domain.model.TireParams
import com.google.gson.annotations.SerializedName

data class TireTypeDTO(
    @SerializedName("tires_type")
    val typeTires: Int
) {
    fun toTireType(): TireParams.Type? = TireOptionsManager.getTypeEnum(typeTires)
}
