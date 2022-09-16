package anless.fleetmanagement.car_module.data.utils

import anless.fleetmanagement.car_module.domain.model.TireParams

class TireOptionsManager {
    companion object {
        private object Condition {
            const val NEW = 1
            const val GOOD = 2
            const val FOR_ONE_SEASON = 3
        }

        private object Type {
            const val SUMMER = 1
            const val WINTER_STUDDED = 3
            const val WINTER_STUDLESS = 4
        }

        fun getConditionEnum(condition: Int) =
            when (condition) {
                Condition.NEW -> TireParams.Condition.NEW
                Condition.GOOD -> TireParams.Condition.GOOD
                Condition.FOR_ONE_SEASON -> TireParams.Condition.FOR_ONE_SEASON
                else -> null
            }

        fun getConditionInt(condition: TireParams.Condition) =
            when (condition) {
                TireParams.Condition.NEW -> Condition.NEW
                TireParams.Condition.GOOD -> Condition.GOOD
                TireParams.Condition.FOR_ONE_SEASON -> Condition.FOR_ONE_SEASON
            }

        fun getTypeEnum(type: Int) =
            when (type) {
                Type.SUMMER -> TireParams.Type.SUMMER
                Type.WINTER_STUDDED -> TireParams.Type.WINTER_STUDDED
                Type.WINTER_STUDLESS -> TireParams.Type.WINTER_STUDLESS
                else -> null
            }

        fun getTypeInt(type: TireParams.Type) =
            when (type) {
                TireParams.Type.SUMMER -> Type.SUMMER
                TireParams.Type.WINTER_STUDDED -> Type.WINTER_STUDDED
                TireParams.Type.WINTER_STUDLESS -> Type.WINTER_STUDLESS
            }
    }
}