package anless.fleetmanagement.car_module.domain.model

data class Contract(
    val id: String,
    val createdat: String,
    val pickupdate: String,
    val dropoffdate: String,
    val closeat: String,
    val fin: String,
    val fleetID: String,
    val taskID: Long,
    val additionalDriverIDS: String,
    val companyID: String,
    val sourceID: String,
    val ownerID: String,
    val userID: String,
    val currencyID: String,
    val days: String,
    val monthlyPaymentSum: String,
    val monthlyMileageLimit: String,
    val mileagePerDay: String,
    val mileageAll: String,
    val mileageExtraPrice: String,
    val refuelingPrice: String,
    val debt: String,
    val debtFines: String,
    val office1ID: String,
    val office2ID: String,
    val clientID: String,
    val entityID: String,
    val ccID: String,
    val ccOfferID: String,
    val docSetID: String,
    val aprokID: String,
    val dpm: String,
    val fromTemplate: String,
    val company: Company,
    val station1: Map<String, String>,
    val station2: Map<String, String>,
    val currency: Currency,
    val client: Client,
    val refid: String,
    val vehicleID: Long,
    val status: Long
) {
    data class Company (
        val id: String,
        val title: String,
        val shortCode: String,
        val alertEmail: String,
        val contactCenterEmail: String,
        val web: String,
        val logoEmail: String,
        val logoOffer: String,
        val active: String
    )

    data class Currency (
        val id: String,
        val title: String,
        val shortTitle: String,
        val shortCode: String,
        val sort: String,
        val active: String
    )

    data class Client (
        val id: Long,
        val fullTitle: String
    )
}