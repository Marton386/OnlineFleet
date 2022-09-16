package anless.fleetmanagement.car_module.domain.repository

import anless.fleetmanagement.car_module.domain.model.*
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface CarOptionsRepository {
    suspend fun getRelocations(): Result<List<Relocation>>
    suspend fun getCurrentTires(idCar: Int): Result<TireParams.Type>
    suspend fun getReservations(reservationSearch: ReservationSearch): Result<List<ReservationItem>>
    suspend fun getCarWashes(): Result<List<SimpleItem>>
    suspend fun getCarPosition(idCar: Int): Result<CarPosition>
}