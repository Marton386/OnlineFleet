package anless.fleetmanagement.car_module.domain.model

data class Overprices(
    val mileageExcess: Int,
    val fuelPay: Boolean,
    val carWashPay: Boolean,
    val fineCost: Int
)
