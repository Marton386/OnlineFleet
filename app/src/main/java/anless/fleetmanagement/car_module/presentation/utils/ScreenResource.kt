package anless.fleetmanagement.car_module.presentation.utils

import anless.fleetmanagement.R

class ScreenResource {
    companion object {
        fun getScreenResource(screen: ActionParamScreenManager.OptionScreen) =
            when (screen) {
                ActionParamScreenManager.OptionScreen.CAR_DETAILS -> R.id.carDetailsFragment
                ActionParamScreenManager.OptionScreen.STATION -> R.id.stationFragment
                ActionParamScreenManager.OptionScreen.RESERVATION_NUMBER -> R.id.reservationFragment
                ActionParamScreenManager.OptionScreen.MILEAGE -> R.id.mileageFragment
                ActionParamScreenManager.OptionScreen.FUEL_AND_CLEAN -> R.id.fuelAndCleanFragment
                //ActionScreensManager.OptionScreen.CLEAR_STATE -> R.id.clearStateFragment
                //ActionScreensManager.OptionScreen.EXTRAS -> R.id.extrasFragment
                ActionParamScreenManager.OptionScreen.MAINTENANCE -> R.id.maintenanceFragment
                ActionParamScreenManager.OptionScreen.INVOICE_NUMBER -> R.id.invoiceFragment
                ActionParamScreenManager.OptionScreen.PRICE -> R.id.priceFragment
                ActionParamScreenManager.OptionScreen.WASHING -> R.id.washingFragment
                ActionParamScreenManager.OptionScreen.CONTRACTOR -> R.id.contractorFragment
                //ActionParamScreenManager.OptionScreen.PTI_TYPE -> R.id.maintenanceFragment // merge in one maintenance fragment
                ActionParamScreenManager.OptionScreen.LITERS -> R.id.litersFragment
                ActionParamScreenManager.OptionScreen.ACT -> R.id.actFragment
                //ActionParamScreenManager.OptionScreen.MAINTENANCE_PHOTOS -> R.id.maintenanceFragment // merge in one maintenance fragment
                ActionParamScreenManager.OptionScreen.STOP_DAYS -> R.id.stopDaysFragment
                ActionParamScreenManager.OptionScreen.RUBBER_PARAMS -> R.id.tireFragment
                ActionParamScreenManager.OptionScreen.COMMENT -> R.id.commentFragment
                ActionParamScreenManager.OptionScreen.DROP_OFF_PLACE -> R.id.dropOffPlaceFragment
                ActionParamScreenManager.OptionScreen.CHECK_OVERPRICES -> R.id.overpriceFragment
                ActionParamScreenManager.OptionScreen.SEND_ACTION -> R.id.sendActionFragment
            }

        fun getScreenEnum(idScreen: Int) =
            when (idScreen) {
                R.id.carDetailsFragment -> ActionParamScreenManager.OptionScreen.CAR_DETAILS
                R.id.stationFragment -> ActionParamScreenManager.OptionScreen.STATION
                R.id.reservationFragment -> ActionParamScreenManager.OptionScreen.RESERVATION_NUMBER
                R.id.mileageFragment -> ActionParamScreenManager.OptionScreen.MILEAGE
                R.id.fuelAndCleanFragment -> ActionParamScreenManager.OptionScreen.FUEL_AND_CLEAN
                //R.id.clearStateFragment -> ActionScreensManager.OptionScreen.CLEAR_STATE
                //R.id.extrasFragment -> ActionScreensManager.OptionScreen.EXTRAS
                R.id.maintenanceFragment -> ActionParamScreenManager.OptionScreen.MAINTENANCE
                R.id.invoiceFragment -> ActionParamScreenManager.OptionScreen.INVOICE_NUMBER
                R.id.priceFragment -> ActionParamScreenManager.OptionScreen.PRICE
                R.id.washingFragment -> ActionParamScreenManager.OptionScreen.WASHING
                R.id.contractorFragment -> ActionParamScreenManager.OptionScreen.CONTRACTOR
                R.id.litersFragment -> ActionParamScreenManager.OptionScreen.LITERS
                R.id.actFragment -> ActionParamScreenManager.OptionScreen.ACT
                R.id.stopDaysFragment -> ActionParamScreenManager.OptionScreen.STOP_DAYS
                R.id.tireFragment -> ActionParamScreenManager.OptionScreen.RUBBER_PARAMS
                R.id.commentFragment -> ActionParamScreenManager.OptionScreen.COMMENT
                R.id.dropOffPlaceFragment -> ActionParamScreenManager.OptionScreen.DROP_OFF_PLACE
                R.id.overpriceFragment -> ActionParamScreenManager.OptionScreen.CHECK_OVERPRICES
                R.id.sendActionFragment -> ActionParamScreenManager.OptionScreen.SEND_ACTION
                else -> null
            }
    }
}