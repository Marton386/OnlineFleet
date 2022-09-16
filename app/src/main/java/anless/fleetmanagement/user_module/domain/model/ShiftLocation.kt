package anless.fleetmanagement.user_module.domain.model

import com.google.android.gms.maps.model.LatLng

data class ShiftLocation(
    val locationLatLng: LatLng,
    val idStation: Int
)