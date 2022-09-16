package anless.fleetmanagement.car_module.domain.model

data class TireParams(
    val type: Type,
    val condition: Condition,
    val profile: Int,
    val width: Int,
    val diameter: Int
) {
    enum class Type {
        SUMMER, WINTER_STUDDED, WINTER_STUDLESS
    }

    enum class Condition {
        NEW, GOOD, FOR_ONE_SEASON
    }
}
