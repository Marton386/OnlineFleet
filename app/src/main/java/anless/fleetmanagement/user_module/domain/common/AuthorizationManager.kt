package anless.fleetmanagement.user_module.domain.common

import androidx.lifecycle.LiveData
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.UserHash
import anless.fleetmanagement.user_module.domain.model.UserLoginInfo

interface AuthorizationManager {
    suspend fun checkSession(): Result<Boolean>
    suspend fun login(userLoginInfo: UserLoginInfo): Result<UserHash>
    suspend fun logout(): Result<Boolean>
    fun getHashLiveData(): LiveData<UserHash?>
    fun getHash(): String?
}