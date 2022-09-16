package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.data.utils.Constants
import anless.fleetmanagement.car_module.domain.model.Overprices
import com.google.gson.annotations.SerializedName

data class OverpricesDTO(
    @SerializedName("mileage")
    val mileage: Int,

    @SerializedName("fuel")
    val fuel: Int,

    @SerializedName("clean")
    val clean: Int,

    @SerializedName("fines")
    val fines: List<FineDTO>
) {
    data class FineDTO(
        @SerializedName("Sum")
        val cost: Int,

        @SerializedName("RetentionSum")
        val payedSum: Int
    )

    fun toOverprices() = Overprices(
        mileageExcess = mileage,
        fuelPay = (fuel != Constants.NO_OVERPRICE),
        carWashPay = (clean != Constants.NO_OVERPRICE),
        fineCost = fines.sumOf { fine ->
            fine.cost - fine.payedSum
        }
    )
}