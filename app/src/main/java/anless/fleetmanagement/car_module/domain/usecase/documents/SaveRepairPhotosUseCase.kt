package anless.fleetmanagement.car_module.domain.usecase.documents

import anless.fleetmanagement.car_module.domain.model.SavedPhotoInfo
import anless.fleetmanagement.car_module.domain.repository.DocumentsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class SaveRepairPhotosUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(imagesByteArray: List<ByteArray>): Result<SavedPhotoInfo> {
        return documentsRepository.saveMaintenancePhotos(imagesByteArray)
    }
}