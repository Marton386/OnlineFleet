package anless.fleetmanagement.car_module.domain.usecase.action

import javax.inject.Inject

data class ActionUseCases @Inject constructor(
    val pickUpUseCases: PickUpUseCase,
    val dropOffUseCase: DropOffUseCase,
    val decommissioningUseCase: DecommissioningUseCase,
    val commissioningUseCase: CommissioningUseCase,
    val relocationStartUseCase: RelocationStartUseCase,
    val relocationEndUseCase: RelocationEndUseCase,
    val deliveryUseCase: DeliveryUseCase,
    val maintenanceStartUseCase: MaintenanceStartUseCase,
    val maintenanceEndUseCase: MaintenanceEndUseCase,
    val changeTiresUseCase: ChangeTiresUseCase,
    val refillFuelUseCase: RefillFuelUseCase,
    val washingUseCase: WashingUseCase
)