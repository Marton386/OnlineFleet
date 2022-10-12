package anless.fleetmanagement.car_module.presentation.utils

import anless.fleetmanagement.R

object ActionParamScreenManager {
    enum class OptionScreen {
        CAR_DETAILS,
        STATION,
        RESERVATION_NUMBER,
        MILEAGE,
        FUEL_AND_CLEAN,
        //CLEAR_STATE,
        DROP_OFF_PLACE,
        CHECK_OVERPRICES,
        //EXTRAS,
        //CONTRACT_NUMBER,
        INVOICE_NUMBER,
        PRICE,
        WASHING,
        CONTRACTOR,
        MAINTENANCE,
        //MAINTENANCE_PHOTOS,
        //PTI_TYPE,
        LITERS,
        ACT,
        STOP_DAYS,
        RUBBER_PARAMS,
        COMMENT,
        SEND_ACTION
    }

    private val screensHashMap = hashMapOf(
        OptionScreen.CAR_DETAILS to R.id.carDetailsFragment,
        OptionScreen.STATION to R.id.stationFragment,
        OptionScreen.RESERVATION_NUMBER to R.id.reservationFragment,
        OptionScreen.MILEAGE to R.id.mileageFragment,
        OptionScreen.FUEL_AND_CLEAN to R.id.fuelAndCleanFragment,
        //OptionScreen.EXTRAS to R.id.extrasFragment,
        OptionScreen.MAINTENANCE to R.id.maintenanceFragment,
        OptionScreen.INVOICE_NUMBER to R.id.invoiceFragment,
        OptionScreen.PRICE to R.id.priceFragment,
        OptionScreen.WASHING to R.id.washingFragment,
        OptionScreen.CONTRACTOR to R.id.contractorFragment,
        //OptionScreen.PTI_TYPE to R.id.maintenanceFragment, // merge in one maintenance fragment
        OptionScreen.LITERS to R.id.litersFragment,
        OptionScreen.ACT to R.id.actFragment,
        //OptionScreen.MAINTENANCE_PHOTOS to R.id.maintenanceFragment, // merge in one maintenance fragment
        OptionScreen.STOP_DAYS to R.id.stopDaysFragment,
        OptionScreen.RUBBER_PARAMS to R.id.tireFragment,
        OptionScreen.COMMENT to R.id.commentFragment,
        OptionScreen.DROP_OFF_PLACE to R.id.dropOffPlaceFragment,
        OptionScreen.CHECK_OVERPRICES to R.id.overpriceFragment,
        OptionScreen.SEND_ACTION to R.id.sendActionFragment
    )

    fun getActionScreens(actionStatus: Int): List<OptionScreen>? =
        when (actionStatus) {
            ActionManager.ActionType.PICKUP -> pickup()
            ActionManager.ActionType.PICKUP_WITHOUT_RES -> pickupWithoutResNumb()
            ActionManager.ActionType.DROP_OFF -> dropOff()
            ActionManager.ActionType.DECOMMISSIONING -> decommissioning()
            ActionManager.ActionType.COMMISSIONING -> commissioning()
            ActionManager.ActionType.START_RELOCATION -> relocationStart() // check relocations first
            ActionManager.ActionType.END_RELOCATION -> relocationEnd()
            ActionManager.ActionType.DELIVERY -> delivery()
            ActionManager.ActionType.START_MAINTENANCE -> maintenanceStart()
            ActionManager.ActionType.END_MAINTENANCE -> maintenanceEnd()
            ActionManager.ActionType.TIRE_CHANGE -> changeTires()
            ActionManager.ActionType.REFILL_FUEL -> refillFuel()
            ActionManager.ActionType.WASHING -> washing()
            ActionManager.ActionType.UNKNOWN -> null
            else -> null
        }

    fun getScreenResource(screen: OptionScreen): Int =
        screensHashMap.getValue(screen)

    fun getScreenEnum(idScreen: Int): OptionScreen? {
        val keys = screensHashMap.filterValues { value ->
            value == idScreen
        }.keys

        return if (keys.isNotEmpty()) keys.first() else null
    }


    private fun pickup() = listOf(
        OptionScreen.RESERVATION_NUMBER,
        OptionScreen.MILEAGE,
        OptionScreen.FUEL_AND_CLEAN,
        OptionScreen.ACT,
        OptionScreen.SEND_ACTION
    )

    private fun pickupWithoutResNumb() = listOf(
        OptionScreen.MILEAGE,
        OptionScreen.FUEL_AND_CLEAN,
        OptionScreen.ACT,
        OptionScreen.SEND_ACTION
    )

    private fun dropOff() = listOf(
        OptionScreen.DROP_OFF_PLACE,
        OptionScreen.MILEAGE,
        OptionScreen.FUEL_AND_CLEAN,
        OptionScreen.CHECK_OVERPRICES,
        OptionScreen.ACT,
        OptionScreen.SEND_ACTION
    )

    private fun decommissioning() = listOf(
        OptionScreen.STOP_DAYS,
        OptionScreen.COMMENT,
        OptionScreen.SEND_ACTION
    )

    private fun commissioning() = listOf(
        OptionScreen.MILEAGE,
        OptionScreen.FUEL_AND_CLEAN,
        OptionScreen.COMMENT,
        OptionScreen.SEND_ACTION
    )

    private fun relocationStart() = listOf(
        OptionScreen.STATION,
        OptionScreen.COMMENT,
        OptionScreen.SEND_ACTION
    )

    private fun relocationEnd() = listOf(
        OptionScreen.STATION,
        OptionScreen.MILEAGE,
        OptionScreen.FUEL_AND_CLEAN,
        OptionScreen.SEND_ACTION
    )

    private fun delivery() = listOf(
        OptionScreen.RESERVATION_NUMBER,
        OptionScreen.SEND_ACTION
    )

    private fun maintenanceStart() = listOf(
        OptionScreen.MAINTENANCE,
        OptionScreen.CONTRACTOR,
        OptionScreen.MILEAGE,
        OptionScreen.COMMENT,
        OptionScreen.SEND_ACTION
    )

    private fun maintenanceEnd() = listOf(
        OptionScreen.INVOICE_NUMBER,
        OptionScreen.PRICE,
        OptionScreen.MILEAGE,
        OptionScreen.COMMENT,
        OptionScreen.SEND_ACTION
    )

    private fun changeTires() = listOf(
        OptionScreen.RUBBER_PARAMS,
        OptionScreen.PRICE,
        OptionScreen.SEND_ACTION
    )

    private fun refillFuel() = listOf(
        OptionScreen.LITERS,
        OptionScreen.SEND_ACTION
    )

    private fun washing() = listOf(
        OptionScreen.WASHING,
        OptionScreen.PRICE,
        OptionScreen.SEND_ACTION
    )
}