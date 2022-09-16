package anless.fleetmanagement.user_module.domain.model

import anless.fleetmanagement.station_module.domain.model.Station

data class Shift(
    val startTime: Long,
    val station: Station
)