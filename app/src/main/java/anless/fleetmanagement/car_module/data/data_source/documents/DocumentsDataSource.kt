package anless.fleetmanagement.car_module.data.data_source.documents

import anless.fleetmanagement.car_module.domain.model.SavedPhotoInfo
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo

interface DocumentsDataSource {
    suspend fun saveRentAct(
        sourceInfo: SourceInfo,
        imageByteArray: ByteArray
    ): Result<SavedPhotoInfo>

    suspend fun saveMaintenancePhotos(
        sourceInfo: SourceInfo,
        imagesByteArray: List<ByteArray>
    ): Result<SavedPhotoInfo>

    suspend fun downloadInsurance(
        sourceInfo: SourceInfo,
        filename: String
    ): Result<ByteArray>

    suspend fun getMinimalVersionApp(): Result<Int>
}