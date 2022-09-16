package anless.fleetmanagement.car_module.domain.model

import com.google.android.gms.maps.model.LatLng

data class CarPosition(
    val lastUpdate: Long?,
    val speed: Double,
    val voltage: Double,
    val location: LatLng
)
