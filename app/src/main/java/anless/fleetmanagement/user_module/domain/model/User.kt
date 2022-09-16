package anless.fleetmanagement.user_module.domain.model

data class User(
    val id: Int,
    val name: String,
    val post: String,
    val rating: Double,
    val avatarFilename: String,
    val timezone: Int,
    val awards: String
    // station? id, name
)
