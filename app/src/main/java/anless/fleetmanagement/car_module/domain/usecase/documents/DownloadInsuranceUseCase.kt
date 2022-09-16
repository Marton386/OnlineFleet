package anless.fleetmanagement.car_module.domain.usecase.documents

import anless.fleetmanagement.car_module.domain.model.FileUrl
import anless.fleetmanagement.car_module.domain.repository.DocumentsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class DownloadInsuranceUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
){

    suspend operator fun invoke(filename: String): Result<ByteArray> {
        return documentsRepository.downloadInsurance(filename)
    }
}