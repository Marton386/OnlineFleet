package anless.fleetmanagement.user_module.data.model

import anless.fleetmanagement.user_module.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name1")
    val lastName: String,

    @SerializedName("name2")
    val firstName: String,

    @SerializedName("name3")
    val patronymic: String,

    @SerializedName("post")
    val post: String,

    @SerializedName("rating")
    val rating: Double,

    @SerializedName("pic")
    val imgName: String,

    @SerializedName("awards")
    val awards: List<Award>,

    @SerializedName("tasks_office")
    val idStation: Int,

    @SerializedName("timezone")
    val timezone: Int
) {
    data class Award(
        @SerializedName("icon")
        val emojiCode: String
    )

    fun toUser(): User {
        return User(
            id = id,
            name = "$lastName $firstName $patronymic",
            post = post,
            rating = rating,
            avatarFilename = imgName,
            timezone = timezone,
            // station_id
            awards = awards.joinToString(separator = " ")
        )
    }
}
