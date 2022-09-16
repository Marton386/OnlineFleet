package anless.fleetmanagement.car_module.data.utils

import anless.fleetmanagement.car_module.presentation.utils.MaintenanceType
import anless.fleetmanagement.car_module.presentation.utils.RepairType

object RepairWorks {
    const val BODY_WORKS = 1
    const val LOCKSMITH_WORKS = 2
    const val MAINTENANCE = 3

    object TypeMaintenance {
        const val TO_1 = 1
        const val TO_2 = 2
        const val TO_3 = 3
        const val TO_4 = 4
        const val TO_5 = 5
        const val TO_6 = 6
        const val TO_7 = 7
        const val TO_8 = 8
        const val TO_9 = 9
        const val TO_10 = 10
        const val TO_11 = 11
        const val TO_12 = 12
        const val TO_13 = 13
        const val TO_14 = 14
        const val TO_15 = 15
    }

    fun getWorksType(repairType: RepairType) =
        when(repairType) {
            RepairType.BODY_WORKS -> BODY_WORKS
            RepairType.LOCKSMITH_WORKS -> LOCKSMITH_WORKS
            RepairType.MAINTENANCE -> MAINTENANCE
        }

    fun getMaintenanceType(maintenanceType: MaintenanceType) =
        when(maintenanceType) {
            MaintenanceType.TO_1 -> TypeMaintenance.TO_1
            MaintenanceType.TO_2 -> TypeMaintenance.TO_2
            MaintenanceType.TO_3 -> TypeMaintenance.TO_3
            MaintenanceType.TO_4 -> TypeMaintenance.TO_4
            MaintenanceType.TO_5 -> TypeMaintenance.TO_5
            MaintenanceType.TO_6 -> TypeMaintenance.TO_6
            MaintenanceType.TO_7 -> TypeMaintenance.TO_7
            MaintenanceType.TO_8 -> TypeMaintenance.TO_8
            MaintenanceType.TO_9 -> TypeMaintenance.TO_9
            MaintenanceType.TO_10 -> TypeMaintenance.TO_10
            MaintenanceType.TO_11 -> TypeMaintenance.TO_11
            MaintenanceType.TO_12 -> TypeMaintenance.TO_12
            MaintenanceType.TO_13 -> TypeMaintenance.TO_13
            MaintenanceType.TO_14 -> TypeMaintenance.TO_14
            MaintenanceType.TO_15 -> TypeMaintenance.TO_15
        }
}