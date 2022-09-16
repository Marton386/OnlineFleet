package anless.fleetmanagement.station_module.domain.usecase

import anless.fleetmanagement.station_module.domain.model.Station
import anless.fleetmanagement.station_module.domain.repository.StationRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    suspend operator fun invoke(): Result<List<Station>> {
        return stationRepository.getStations()
    }
}