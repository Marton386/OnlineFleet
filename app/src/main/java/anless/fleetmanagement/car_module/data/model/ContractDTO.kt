package anless.fleetmanagement.car_module.data.model

import anless.fleetmanagement.car_module.domain.model.Contract
import anless.fleetmanagement.car_module.domain.model.LightContract
import com.google.gson.annotations.SerializedName

data class ContractDTO (
    val id: String,
    val createdat: String,
    val pickupdate: String,
    val dropoffdate: String,
    val closeat: String,
    val fin: String,

    @SerializedName("fleet_id")
    val fleetID: String,

    @SerializedName("task_id")
    val taskID: Long,

    @SerializedName("additional_driver_ids")
    val additionalDriverIDS: String,

    @SerializedName("company_id")
    val companyID: String,

    @SerializedName("source_id")
    val sourceID: String,

    @SerializedName("owner_id")
    val ownerID: String,

    @SerializedName("user_id")
    val userID: String,

    @SerializedName("currency_id")
    val currencyID: String,

    val days: String,

    @SerializedName("monthly_payment_sum")
    val monthlyPaymentSum: String,

    @SerializedName("monthly_mileage_limit")
    val monthlyMileageLimit: String,

    @SerializedName("mileage_per_day")
    val mileagePerDay: String,

    @SerializedName("mileage_all")
    val mileageAll: String,

    @SerializedName("mileage_extra_price")
    val mileageExtraPrice: String,

    @SerializedName("refueling_price")
    val refuelingPrice: String,

    val debt: String,

    @SerializedName("debt_fines")
    val debtFines: String,

    @SerializedName("office1_id")
    val office1ID: String,

    @SerializedName("office2_id")
    val office2ID: String,

    @SerializedName("client_id")
    val clientID: String,

    @SerializedName("entity_id")
    val entityID: String,

    @SerializedName("cc_id")
    val ccID: String,

    @SerializedName("cc_offer_id")
    val ccOfferID: String,

    @SerializedName("doc_set_id")
    val docSetID: String,

    @SerializedName("aprok_id")
    val aprokID: String,

    val dpm: String,

    @SerializedName("from_template")
    val fromTemplate: String,

    val company: Company,
    val station1: Map<String, String>,
    val station2: Map<String, String>,
    val currency: Currency,
    val client: Client,
    val refid: String,

    @SerializedName("vehicle_id")
    val vehicleID: Long,

    val status: Long
) {
    data class Company (
        val id: String,
        val title: String,

        @SerializedName("short_code")
        val shortCode: String,

        @SerializedName("alert_email")
        val alertEmail: String,

        @SerializedName("contact_center_email")
        val contactCenterEmail: String,

        val web: String,

        @SerializedName("logo_email")
        val logoEmail: String,

        @SerializedName("logo_offer")
        val logoOffer: String,

        val active: String
    )

    data class Currency (
        val id: String,
        val title: String,

        @SerializedName("short_title")
        val shortTitle: String,

        @SerializedName("short_code")
        val shortCode: String,

        val sort: String,
        val active: String
    )

    data class Client (
        val id: Long,

        @SerializedName("full_title")
        val fullTitle: String
    )

    fun toContract(): Contract = Contract(
        id = this.id,
        createdat = this.createdat,
        pickupdate = this.pickupdate,
        dropoffdate = this.dropoffdate,
        closeat = this.closeat,
        fin = this.fin,
        fleetID = this.fleetID,
        taskID = this.taskID,
        additionalDriverIDS = this.additionalDriverIDS,
        companyID = this.companyID,
        sourceID = this.sourceID,
        ownerID = this.ownerID,
        userID = this.userID,
        currencyID = this.currencyID,
        days = this.days,
        monthlyPaymentSum = this.monthlyPaymentSum,
        monthlyMileageLimit = this.monthlyMileageLimit,
        mileagePerDay = this.mileagePerDay,
        mileageAll = this.mileageAll,
        mileageExtraPrice = this.mileageExtraPrice,
        refuelingPrice = this.refuelingPrice,
        debt = this.debt,
        debtFines = this.debtFines,
        office1ID = this.office1ID,
        office2ID = this.office2ID,
        clientID = this.clientID,
        entityID = this.entityID,
        ccID = this.ccID,
        ccOfferID = this.ccOfferID,
        docSetID = this.docSetID,
        aprokID = this.aprokID,
        dpm = this.dpm,
        fromTemplate = this.fromTemplate,
        company = Contract.Company (
            id = this.company.id,
            title = this.company.title,
            shortCode = this.company.shortCode,
            alertEmail = this.company.alertEmail,
            contactCenterEmail = this.company.contactCenterEmail,
            web = this.company.web,
            logoEmail = this.company.logoEmail,
            logoOffer = this.company.logoOffer,
            active = this.company.active
        ),
        station1 = this.station1,
        station2 = this.station2,
        currency = Contract.Currency(
            id = this.currency.id,
            title = this.currency.title,
            shortTitle = this.currency.shortTitle,
            shortCode = this.currency.shortCode,
            sort = this.currency.sort,
            active = this.currency.active
        ),
        client = Contract.Client(
            id = this.client.id,
            fullTitle = this.client.fullTitle
        ),
        refid = this.refid,
        vehicleID = this.vehicleID,
        status = this.status
    )

    fun toLightContract(): LightContract = LightContract(
        refid = this.refid,
        vehicleID = this.vehicleID,
        status = this.status,
        client = LightContract.Client(
            id = this.client.id,
            fullTitle = this.client.fullTitle
        )
    )
}