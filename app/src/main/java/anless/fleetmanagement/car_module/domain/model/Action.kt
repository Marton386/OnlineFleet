package anless.fleetmanagement.car_module.domain.model

class Action {
    data class Pickup(
        val reservation: Reservation,
        val idCar: Int,
        val idStation: Int,
        val carParam: CarParam,
        //val extras: String,
        val actPhotoFileName: String
        //val contractNumber: String
    )

    data class DropOff(
        val idCar: Int,
        val idStation: Int,
        val carParam: CarParam,
        val actPhotoFileName: String,
    )

    data class Decommissioning(
        val idCar: Int,
        val idStation: Int,
        val daysStop: Int,
        val comment: String
    )

    data class Commissioning(
        val idCar: Int,
        val licensePlate: String,
        val idStation: Int,
        val carParam: CarParam,
        val comment: String
    )

    class Relocation {
        data class Start(
            val idCar: Int,
            val idStation: Int,
            val comment: String
        )

        data class End(
            val idCar: Int,
            val idStation: Int,
            val carParam: CarParam
        )
    }

    data class Delivery(
        val reservation: Reservation,
        val idCar: Int,
        val idStation: Int,
    )

    class Maintenance {
        data class Start(
            val idCar: Int,
            val idStation: Int,
            val mileage: Int,
            val params: RepairParams,
            val contractor: String,
            val comment: String
        )

        data class End(
            val idCar: Int,
            val idStation: Int,
            val mileage: Int,
            val invoiceNumber: String,
            val price: Int,
            val comment: String
        )
    }

    data class ChangeTires(
        val idCar: Int,
        val tireParams: TireParams,
        val price: Int
    )

    data class RefillFuel(
        val idCar: Int,
        val litres: Int,
    )

    data class Washing(
        val idCar: Int,
        val idCarWash: Int,
        val price: Int
    )
}