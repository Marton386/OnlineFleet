package anless.fleetmanagement.car_module.data.model

import com.google.gson.annotations.SerializedName

data class InsuranceFilename(
    @SerializedName("filename")
    val filename: String
)