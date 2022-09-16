package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.ReservationItem
import anless.fleetmanagement.core.utils.ErrorCodes
import com.google.gson.annotations.SerializedName

data class ReservationDTO(
    @SerializedName("id_conf")
    val reservation: String,

    @SerializedName("name")
    val lastName: String,

    @SerializedName("name2")
    val firstName: String,

    @SerializedName("company")
    val company: CompanyDTO,

    @SerializedName("source")
    val source: SourceDTO,

    @SerializedName("error_code")
    val errorCode: Int
) {
    data class CompanyDTO(
        @SerializedName("short_code")
        val shortCode: String
    )

    data class SourceDTO(
        @SerializedName("id")
        val id: Int,

        @SerializedName("short_code")
        val name: String
    )

    fun toReservationItem() = ReservationItem(
        resNumber = reservation,
        clientName = "$lastName $firstName",
        company = company.shortCode,
        source = ReservationItem.Source(
            id = source.id,
            name = source.name
        ),
        error = if (errorCode == ErrorCodes.RES_NO_ERROR) null else errorCode
    )
}
