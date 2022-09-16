package anless.fleetmanagement.core.utils.authorization

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.user_module.domain.common.AuthorizationManager
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo
import anless.fleetmanagement.user_module.domain.repository.SessionRepository

class AuthorizationManagerImpl(
    private val sessionRepository: SessionRepository,
    private val hashManagerPreferences: HashManagerPreferences
) : AuthorizationManager {

    private val hash = hashManagerPreferences.getHashLiveData()

    override suspend fun checkSession(): Result<Boolean> {
        val userHash = hash.value

        userHash?.let { strHash ->
            val result = sessionRepository.checkSession(
                SourceInfo(
                    userHash = UserHash(strHash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )

            if (result is Result.Error) {
                if (result.errorCode == ErrorCodes.SESSION_IS_CLOSED) {
                    Log.i("AuthorizationManager", "session closed")
                    clearUserData()
                }
            }
            return result

        }

        return Result.Error(ErrorCodes.UNAUTHORIZED)
    }

    override suspend fun login(userLoginInfo: UserLoginInfo): Result<UserHash> {
        val result = sessionRepository.createSession(userLoginInfo)

        if (result is Result.Success) {
            hashManagerPreferences.setHash(userHash = result.data)
        }

        return result
    }

    override suspend fun logout(): Result<Boolean> {
        val userHash = hash.value

        userHash?.let { strHash ->
            val result = sessionRepository.clearSession(
                sourceInfo = SourceInfo(
                    userHash = UserHash(strHash),
                    appVersionCode = BuildConfig.VERSION_CODE
                )
            )

            if (result is Result.Success) {
                clearUserData()
            }

            return result
        }

        return Result.Success(false)
    }

    private suspend fun clearUserData() {
        hashManagerPreferences.clearHash()
        Log.i("AuthorizationManager", "data_cleared")

    }

    override fun getHashLiveData(): LiveData<UserHash?> {
        return hash.map { hash ->
            hash?.let { strHash ->
                UserHash(strHash)
            }
        }
    }

    override fun getHash(): String? {
        return hash.value
    }
}