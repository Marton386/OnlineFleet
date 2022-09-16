package anless.fleetmanagement.car_module.domain.repository

import anless.fleetmanagement.car_module.domain.model.SavedPhotoInfo
import anless.fleetmanagement.core.utils.data_result.Result

interface DocumentsRepository {

    suspend fun savePhotoAct(imageByteArray: ByteArray): Result<SavedPhotoInfo>

    suspend fun saveMaintenancePhotos(imagesByteArray: List<ByteArray>): Result<SavedPhotoInfo>

    suspend fun downloadInsurance(filename: String): Result<ByteArray>

    suspend fun requireUpdateApp(): Result<Boolean>
}