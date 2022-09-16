package anless.fleetmanagement.car_module.data.repository

import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.car_module.data.data_source.documents.DocumentsDataSource
import anless.fleetmanagement.car_module.domain.model.SavedPhotoInfo
import anless.fleetmanagement.car_module.domain.repository.DocumentsRepository
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash

class DocumentsRepositoryImpl(
    private val authorizationManager: AuthorizationManager,
    private val documentsDataSource: DocumentsDataSource
) : DocumentsRepository {
    override suspend fun savePhotoAct(imageByteArray: ByteArray): Result<SavedPhotoInfo> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return documentsDataSource.saveRentAct(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                imageByteArray = imageByteArray
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun saveMaintenancePhotos(imagesByteArray: List<ByteArray>): Result<SavedPhotoInfo> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return documentsDataSource.saveMaintenancePhotos(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                imagesByteArray = imagesByteArray
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun downloadInsurance(filename: String): Result<ByteArray> {
        val hash = authorizationManager.getHash()

        if (hash != null) {
            return documentsDataSource.downloadInsurance(
                sourceInfo = SourceInfo(
                    userHash = UserHash(hash),
                    appVersionCode = BuildConfig.VERSION_CODE
                ),
                filename = filename
            )
        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun requireUpdateApp(): Result<Boolean> {
        return when (val result = documentsDataSource.getMinimalVersionApp()) {
            is Result.Success -> Result.Success(data = result.data > BuildConfig.VERSION_CODE)
            is Result.Error -> Result.Error(result.errorCode)
        }
    }
}