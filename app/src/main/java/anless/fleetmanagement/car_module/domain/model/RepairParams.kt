package anless.fleetmanagement.car_module.domain.model

import anless.fleetmanagement.car_module.presentation.utils.MaintenanceType
import anless.fleetmanagement.car_module.presentation.utils.RepairType

data class RepairParams(
    val typeRepair: RepairType,
    val typeTO: MaintenanceType?,
    val repairPhotosFilename: String?
)