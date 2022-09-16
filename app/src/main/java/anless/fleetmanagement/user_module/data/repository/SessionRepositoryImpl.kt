package anless.fleetmanagement.user_module.data.repository

import anless.fleetmanagement.user_module.data.data_source.session.SessionDataSource
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo
import anless.fleetmanagement.user_module.domain.repository.SessionRepository

class SessionRepositoryImpl(
    private val sessionDataSource: SessionDataSource
) : SessionRepository {
    override suspend fun checkSession(sourceInfo: SourceInfo): Result<Boolean> {
        return sessionDataSource.checkSession(sourceInfo)
    }

    override suspend fun createSession(userLoginInfo: UserLoginInfo): Result<UserHash> {
        return sessionDataSource.createSession(userLoginInfo)
    }

    override suspend fun clearSession(sourceInfo: SourceInfo): Result<Boolean> {
        return sessionDataSource.clearSession(sourceInfo)
    }
}