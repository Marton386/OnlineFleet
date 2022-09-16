package anless.fleetmanagement.car_module.domain.usecase.documents

import anless.fleetmanagement.car_module.domain.model.FileUrl
import anless.fleetmanagement.car_module.domain.repository.CarParamsRepository
import anless.fleetmanagement.car_module.domain.repository.DocumentsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class PrepareInsuranceUseCase @Inject constructor(
    private val carParamsRepository: CarParamsRepository
){

    suspend operator fun invoke(filename: String): Result<Boolean> {
        return carParamsRepository.prepareInsurance(filename)
    }
}