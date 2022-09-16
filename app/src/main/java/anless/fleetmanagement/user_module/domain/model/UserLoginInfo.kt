package anless.fleetmanagement.user_module.domain.model

data class UserLoginInfo(
    val login: String,
    val password: String,
    val appVersion: Int
)
