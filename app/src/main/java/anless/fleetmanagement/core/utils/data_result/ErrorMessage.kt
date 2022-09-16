package anless.fleetmanagement.core.utils.data_result

import java.lang.Exception

data class ErrorMessage(
    val code: Int? = null,
    val mess: Exception? = null
)
