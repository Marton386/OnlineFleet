package anless.fleetmanagement.car_module.presentation.utils

import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Car

object ActionManager {
    object ActionType {
        const val UNKNOWN = 0
        const val PICKUP = 1
        const val DROP_OFF = 2
        const val DECOMMISSIONING = 3
        const val COMMISSIONING = 4
        const val START_RELOCATION = 5
        const val END_RELOCATION = 6
        const val DELIVERY = 7
        const val START_MAINTENANCE = 8
        const val END_MAINTENANCE = 9
        const val TIRE_CHANGE = 10
        const val REFILL_FUEL = 11
        const val WASHING = 12
    }

    fun getAvailableActions(car: Car): List<Int> =
        when (car.status) {
            CarStatus.UNKNOWN -> listOf(
                ActionType.PICKUP,
                ActionType.DROP_OFF,
                ActionType.DECOMMISSIONING,
                ActionType.COMMISSIONING,
                ActionType.START_RELOCATION,
                ActionType.END_RELOCATION,
                ActionType.DELIVERY,
                ActionType.START_MAINTENANCE,
                ActionType.END_MAINTENANCE,
                ActionType.TIRE_CHANGE,
                ActionType.REFILL_FUEL,
                ActionType.WASHING
            )

            CarStatus.LEASED_NOW -> if (car.isCorporate) {
                listOf(
                    ActionType.DROP_OFF,
                    ActionType.START_MAINTENANCE,
                    ActionType.REFILL_FUEL,
                    ActionType.WASHING,
                    ActionType.TIRE_CHANGE
                )
            } else {
                listOf(
                    ActionType.DROP_OFF,
                    ActionType.START_MAINTENANCE,
                    ActionType.TIRE_CHANGE
                )
            }

            CarStatus.ON_PARKING -> listOf(
                ActionType.PICKUP,
                ActionType.DECOMMISSIONING,
                ActionType.START_RELOCATION,
                ActionType.DELIVERY,
                ActionType.REFILL_FUEL,
                ActionType.WASHING,
                ActionType.TIRE_CHANGE
            )

            CarStatus.DECOMMISSIONED -> listOf(
                ActionType.COMMISSIONING,
                ActionType.START_RELOCATION,
                ActionType.START_MAINTENANCE,
                ActionType.REFILL_FUEL,
                ActionType.WASHING,
                ActionType.TIRE_CHANGE
            )

            CarStatus.TRANSPORTING -> listOf(
                ActionType.END_RELOCATION,
                ActionType.REFILL_FUEL,
                ActionType.WASHING,
                ActionType.TIRE_CHANGE
            )

            CarStatus.DELIVERY -> listOf(
                ActionType.PICKUP,
                ActionType.START_RELOCATION,
                ActionType.REFILL_FUEL,
                ActionType.WASHING,
                ActionType.TIRE_CHANGE
            )
            CarStatus.MAINTENANCE,
            CarStatus.MAINTENANCE_BY_CLIENT -> listOf(
                ActionType.END_MAINTENANCE,
                ActionType.REFILL_FUEL,
                ActionType.WASHING,
                ActionType.TIRE_CHANGE
            )

            else -> listOf()
        }

    fun getAvailableExtraAction(action: Int) =
        when(action) {
            ActionType.DECOMMISSIONING -> ActionType.START_MAINTENANCE
            //ActionType.END_MAINTENANCE -> ActionType.COMMISSIONING // car may be leased now
            //ActionType.DROP_OFF -> ActionType.START_RELOCATION // auto when drop off on address
            ActionType.DROP_OFF -> ActionType.DECOMMISSIONING
            else -> null
        }

    fun getTitleResString(status: Int) =
        when (status) {
            ActionType.PICKUP -> R.string.pick_up
            ActionType.DROP_OFF -> R.string.drop_off
            ActionType.DECOMMISSIONING -> R.string.decommissioning
            ActionType.COMMISSIONING -> R.string.commissioning
            ActionType.START_RELOCATION -> R.string.start_relocation
            ActionType.END_RELOCATION -> R.string.end_relocation
            ActionType.DELIVERY -> R.string.delivery
            ActionType.START_MAINTENANCE -> R.string.start_maintenance
            ActionType.END_MAINTENANCE -> R.string.end_maintenance
            ActionType.TIRE_CHANGE -> R.string.change_tires
            ActionType.REFILL_FUEL -> R.string.refill_fuel
            ActionType.WASHING -> R.string.washing
            else -> R.string.unknown
        }
}