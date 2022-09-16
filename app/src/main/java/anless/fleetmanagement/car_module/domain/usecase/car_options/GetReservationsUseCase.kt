package anless.fleetmanagement.car_module.domain.usecase.car_options

import anless.fleetmanagement.car_module.domain.model.ReservationItem
import anless.fleetmanagement.car_module.domain.model.ReservationSearch
import anless.fleetmanagement.car_module.domain.repository.CarOptionsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class GetReservationsUseCase @Inject constructor(
    private val carOptionsRepository: CarOptionsRepository
) {
    suspend operator fun invoke(reservationSearch: ReservationSearch): Result<List<ReservationItem>>{
        return carOptionsRepository.getReservations(reservationSearch)
    }
}