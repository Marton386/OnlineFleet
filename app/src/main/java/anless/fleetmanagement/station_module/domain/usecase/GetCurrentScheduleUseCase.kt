package anless.fleetmanagement.station_module.domain.usecase

import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.station_module.domain.model.ScheduleItem
import anless.fleetmanagement.station_module.domain.repository.StationRepository
import javax.inject.Inject

class GetCurrentScheduleUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {

    suspend operator fun invoke(): Result<List<ScheduleItem>> {
        return stationRepository.getCurrentSchedule()
    }
}