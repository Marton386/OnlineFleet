package anless.fleetmanagement.car_module.data.model

import com.google.gson.annotations.SerializedName

data class SavedMaintenancePhotosInfoDTO(
    @SerializedName("filenames")
    val filenames: String
)
