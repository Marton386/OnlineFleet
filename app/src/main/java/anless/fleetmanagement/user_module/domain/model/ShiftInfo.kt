package anless.fleetmanagement.user_module.domain.model

import com.google.android.gms.maps.model.LatLng

data class ShiftInfo(
    val sourceParam: SourceInfo,
    val location: LatLng
)
