package anless.fleetmanagement.car_module.data.data_source.car_options

import anless.fleetmanagement.car_module.domain.model.*
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface CarOptionsDataSource {
    suspend fun geCurrentTires(sourceInfo: SourceInfo, idCar: Int): Result<TireParams.Type>

    suspend fun getRelocations(sourceInfo: SourceInfo): Result<List<Relocation>>

    suspend fun getReservations(sourceInfo: SourceInfo, reservationSearch: ReservationSearch): Result<List<ReservationItem>>

    suspend fun getCarWashes(sourceInfo: SourceInfo): Result<List<SimpleItem>>

    suspend fun getCarPosition(sourceInfo: SourceInfo, idCar: Int): Result<CarPosition>
}