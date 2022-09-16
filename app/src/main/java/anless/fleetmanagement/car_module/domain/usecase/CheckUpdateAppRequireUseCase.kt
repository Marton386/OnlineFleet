package anless.fleetmanagement.car_module.domain.usecase

import anless.fleetmanagement.car_module.domain.repository.DocumentsRepository
import anless.fleetmanagement.core.utils.data_result.Result
import javax.inject.Inject

class CheckUpdateAppRequireUseCase @Inject constructor(
    private val documentsRepository: DocumentsRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return documentsRepository.requireUpdateApp()
    }
}