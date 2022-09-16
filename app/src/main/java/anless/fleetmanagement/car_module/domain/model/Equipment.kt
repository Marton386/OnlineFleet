package anless.fleetmanagement.car_module.domain.model

import anless.fleetmanagement.car_module.domain.utils.OptionalEquipment

data class Equipment(
    val code: String,
    val type: OptionalEquipment,
    val description: String
)
