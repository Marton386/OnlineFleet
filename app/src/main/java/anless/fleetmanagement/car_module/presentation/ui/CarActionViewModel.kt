package anless.fleetmanagement.car_module.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.*
import anless.fleetmanagement.car_module.domain.usecase.action.ActionUseCases
import anless.fleetmanagement.car_module.presentation.utils.*
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarActionViewModel @Inject constructor(
    private val actionUseCases: ActionUseCases
) : ViewModel() {

    companion object {
        const val TAG = "CarActionViewModel"
        private val SCREEN_SELECTING_ACTION = ActionParamScreenManager.OptionScreen.CAR_DETAILS
        private val SCREEN_SEND_ACTION = ActionParamScreenManager.OptionScreen.SEND_ACTION
        private val SCREEN_COMMENT = ActionParamScreenManager.OptionScreen.COMMENT
        private const val START_RELOCATION_ACTION = ActionManager.ActionType.START_RELOCATION
        private const val END_RELOCATION_ACTION = ActionManager.ActionType.END_RELOCATION
    }

    //val _actionStatus = MutableSharedFlow<Int>()
    private var prevAction: Int? = null
    private var prevActionSendMessage: Int? = null
    private var action: Int? = null

    //private var extraAction: Int? = null
    private val _extraAction = MutableStateFlow<Int?>(value = null)
    val extraAction = _extraAction.asStateFlow()
/*    private val _extraActionIsAvailable = MutableSharedFlow<Boolean>()
    val extraActionIsAvailable = _extraActionIsAvailable.asSharedFlow()*/

    private val _idCar = MutableStateFlow<Int?>(value = null)
    val idCar = _idCar.asStateFlow()

    private val _extraActionIsAvailable = MutableStateFlow(value = false)
    val extraActionIsAvailable = _extraActionIsAvailable.asStateFlow()

    private val _navigateNext = Channel<Int>(Channel.BUFFERED)
    val navigateNext = _navigateNext.receiveAsFlow()

    private var currentScreen: ActionParamScreenManager.OptionScreen? = SCREEN_SELECTING_ACTION
    private var screensList: List<ActionParamScreenManager.OptionScreen>? = null
    private var isStartRelocationAllowed = false

    private val _state = MutableStateFlow<CarActionState>(CarActionState.Empty)
    val state = _state.asStateFlow()
    /* private val _state = MutableSharedFlow<CarActionState>()
     val state = _state.asSharedFlow()*/

    private var actionSent = false
/*
    private val _state = MutableSharedFlow<CarActionState>()
    val state = _state.asSharedFlow()*/


    // action params--------------
    private var car: Car? = null

    // reservation
    var reservationNumber: String? = null

    //private var idCar: Int? = null
    private var licensePlate: String? = null
    private var idStation: Int? = null
    private var idStationOpenedShift: Int? = null
    private var tireParams: TireParams? = null
    private var price: Int? = null
    private var comment: String? = null
    private var litres: Int? = null
    private var days: Int? = null
    private var reservation: Reservation? = null
    private var idCarWash: Int? = null
    private var mileage: Int? = null
    private var fuel: Int? = null
    private var cleanState: Int? = null
    private var filenamePhotoAct: String? = null
    private var invoiceNumber: String? = null
    private var repairParams: RepairParams? = null
    private var contractor: String? = null
    private var dropOffPlace: DropOffPlace? = null

    fun unselectCar() {
        _idCar.value = null
    }

    fun isCarSelected() = _idCar.value != null

    fun isCarAction() = action != null

    fun setCarId(id: Int) {
        clearAll()
        _idCar.value = id
    }

    fun setCar(_car: Car) {
        car = _car
        if (idStation == null) {
            idStation = _car.stationInfo.idStation
        }

        licensePlate = _car.carInfo.licensePlate
    }

    fun getCar() = car

    fun getCarId() = idCar.value

    fun isActionClear() = action == null

    fun isActionSent() = _state.value is CarActionState.Success

    fun isExtraActionAvailable() = _extraActionIsAvailable.value

    fun getActionTitle(): Int? {
        return ActionManager.getTitleResString(action ?: return null)
    }

    //fun getExtraAction() = extraAction

    fun getCarInfo(): CarParam? {
        return CarParam(
            mileage = mileage ?: return null,
            fuel = fuel ?: return null,
            cleanState = cleanState ?: return null
        )
    }

    fun getCarTitle(): String? {
        val car = car ?: return null

        return "${car.carInfo.model}\n${car.carInfo.licensePlate.uppercase()}"
    }

    fun getCarLicensePlate(): String? {
        _idCar.value?.let { idCar ->
/*            if (idCar == Constants.ID_NEW_CAR) {
                return licensePlate
            } else {
                car?.let { _car ->
                    return _car.carInfo.licensePlate
                }
            }*/
            return licensePlate
        }

        return null
    }

    fun getDefaultStationId(): Int? =
        when (action) {
            START_RELOCATION_ACTION, END_RELOCATION_ACTION -> idStationOpenedShift
            else -> null
        }

    fun setAction(idAction: Int) {
        if (idAction == START_RELOCATION_ACTION && !isStartRelocationAllowed) {
            _state.value = CarActionState.Error(R.string.need_complete_opened_relocations)
            return
        }
        action = idAction
        screensList = getActionScreens()
        setNextScreen()
    }

    fun setScreensList(idStatus: Int) {
        action = if (idStatus == -1)
            1
        else
            idStatus
        screensList = getNewActionScreens(idStatus)
    }

    fun setRelocations(relocations: List<Relocation>) {
        isStartRelocationAllowed = relocations.isEmpty()
    }

    fun setActionEndRelocation() {
        setAction(ActionManager.ActionType.END_RELOCATION)
    }

    fun setLicensePlate(_licensePlate: String) {
        licensePlate = _licensePlate
    }

    fun setActionAddNewCar() {
        idStation = idStationOpenedShift
        setAction(ActionManager.ActionType.COMMISSIONING)
    }

    private fun updateCurrentScreen(screen: ActionParamScreenManager.OptionScreen) {
        currentScreen = screen
        viewModelScope.launch {
            _navigateNext.send(ScreenResource.getScreenResource(screen))
        }

        if (currentScreen == SCREEN_SEND_ACTION && !(_state.value is CarActionState.Success)) {
            sendAction()
        }
    }

    private fun setNextScreen() {
        screensList?.let { screens ->
            if (screens.isEmpty()) return

            if (currentScreen == SCREEN_SELECTING_ACTION) {
                updateCurrentScreen(screens[0])
            } else {
                val curIndex = screens.indexOf(currentScreen)
                if (curIndex < screens.size - 1) {
                    updateCurrentScreen(screens[curIndex + 1])
                }
            }
        }
    }

    fun setCurrentScreen(curScreen: Int?) {
        curScreen?.let { screen ->
            val enumScreen = getScreenEnum(screen) ?: return
            if (currentScreen != enumScreen) {
                if (enumScreen == SCREEN_SELECTING_ACTION) {
                    clearAction()
                }
                if (enumScreen == SCREEN_SEND_ACTION && _extraAction.value != null) {
                    restoreAction()
                }
                currentScreen = enumScreen
            }
        }
    }

    private fun restoreAction() {
        action = prevAction
        prevActionSendMessage?.let { successMessage ->
            _state.value = CarActionState.Success(successMessage)
        }
    }

// action params--------------

    fun setIdStationOpenedShift(idStationShift: Int) {
        idStationOpenedShift = idStationShift
    }

    fun setStationId(id: Int) {
        idStation = id
        setNextScreen()
    }

    fun setTireParams(params: TireParams) {
        tireParams = params
        setNextScreen()
    }

    fun setPrice(cost: Int) {
        price = cost
        setNextScreen()
    }

    fun setLitres(litresAmount: Int) {
        litres = litresAmount
        setNextScreen()
    }

    fun setDaysAmount(daysAmount: Int) {
        days = daysAmount
        setNextScreen()
    }

    fun setReservation(res: Reservation) {
        reservation = res
        reservationNumber = reservation!!.resNumber
        setNextScreen()
    }

    fun setCarWashId(id: Int) {
        idCarWash = id
        setNextScreen()
    }

    fun setComment(text: String) {
        comment = text
        setNextScreen()
    }

    fun setMileage(_mileage: Int) {
        mileage = _mileage
        setNextScreen()
    }

    fun setFuelAndCleanState(_fuel: Int, state: Int) {
        fuel = _fuel
        cleanState = state

        setNextScreen()
    }

    fun setFilenamePhotoAct(filename: String) {
        filenamePhotoAct = filename
        setNextScreen()
    }

    fun setInvoiceNumber(numberInvoice: String) {
        invoiceNumber = numberInvoice
        setNextScreen()
    }

    fun setOverpricesChecked() {
        setNextScreen()
    }

    fun setDropOffPlace(place: DropOffPlace, commentDropOff: String?) {
        // checking for start relocation
        if (place == DropOffPlace.ON_ADDRESS) {
            commentDropOff?.let { commentDropOffOnAddress ->
                comment = commentDropOffOnAddress
                //extraAction = START_RELOCATION_ACTION
                _extraAction.value = START_RELOCATION_ACTION
            }
        } else {
            //extraAction = null
            _extraAction.value = null
            comment = null
        }

        dropOffPlace = place
        setNextScreen()
    }

    fun setRepairParams(params: RepairParams) {
        repairParams = params
        setNextScreen()
    }

    fun setContractor(_contractor: String) {
        contractor = _contractor
        setNextScreen()
    }

/*    fun setOptionalEquipment(equipmentList: List<Equipment>) {
        optionalEquipment = equipmentList
        setNextScreen()
    }*/


    fun clearAll() {
        if (_idCar.value != null) {
            clearAction()
            clearExtraAction()
            clearParams()
            clearState()
            clearCar()
        }
    }

    private fun clearCar() {
        _idCar.value = null
        car = null
    }

    private fun clearAction() {
        action = null
        //extraAction = null
        //_extraAction.value = null
        prevAction = null
        prevActionSendMessage = null
        //currentScreen = null
        screensList = null
        actionSent = false
        // _state Empty?
    }

    private fun clearExtraAction() {
        _extraAction.value = null
        Log.d(TAG, "extra action cleared")
    }


    private fun clearParams() {
        idStation = null
        idStationOpenedShift = null
        tireParams = null
        price = null
        comment = null
        litres = null
        days = null
        reservation = null
        idCarWash = null
        mileage = null
        fuel = null
        cleanState = null
        filenamePhotoAct = null
        invoiceNumber = null
        repairParams = null
        contractor = null
        dropOffPlace = null
    }

    fun clearState() {
        _state.value = CarActionState.Empty
        /*       viewModelScope.launch {
                   _state.emit(CarActionState.Empty)
               }*/
    }
// ---------------------------

    private fun isActionParamsReady() = isLastScreen() && checkParamsReady()

    fun getCommentRequire() = !checkParamsReady()

    fun sendAction() {
        if (checkParamsReady()) {
            when (action) {
                ActionManager.ActionType.PICKUP -> pickup()
                ActionManager.ActionType.DROP_OFF -> dropOff()
                ActionManager.ActionType.DECOMMISSIONING -> decommissioning()
                ActionManager.ActionType.COMMISSIONING -> commissioning()
                ActionManager.ActionType.START_RELOCATION -> startRelocation()
                ActionManager.ActionType.END_RELOCATION -> endRelocation()
                ActionManager.ActionType.DELIVERY -> deliveryCar()
                ActionManager.ActionType.START_MAINTENANCE -> startMaintenance()
                ActionManager.ActionType.END_MAINTENANCE -> endMaintenance()
                ActionManager.ActionType.TIRE_CHANGE -> tireChange()
                ActionManager.ActionType.REFILL_FUEL -> refillFuel()
                ActionManager.ActionType.WASHING -> washing()
                else -> {}
            }
        } else {
            if (currentScreen == SCREEN_SEND_ACTION) {
                _state.value = CarActionState.Error(R.string.not_enough_parameters)
            }
        }
    }

    ///////////////////////////
    //Для проверки того, почему не всегда отправляются данные через QR-код
    fun getAction(): Int? {
        return action;
    }
    fun getIdCar(): Int? {
        return _idCar.value
    }

    fun getIdStationOpenedShift(): Int? {
        return idStationOpenedShift
    }

    fun getMileage(): Int? {
        return mileage
    }

    fun getFuel(): Int? {
        return fuel
    }

    fun getCleanState(): Int? {
        return cleanState
    }

    fun getFilenamePhotoAct(): String? {
        return filenamePhotoAct
    }
    ///////////////////////////

    private fun checkParamsReady(): Boolean {
        return when (action) {
            ActionManager.ActionType.PICKUP -> {
                _idCar.value != null && idStationOpenedShift != null && mileage != null && fuel != null && cleanState != null && filenamePhotoAct != null
            }
            ActionManager.ActionType.DROP_OFF -> {
                _idCar.value != null && idStationOpenedShift != null && mileage != null && fuel != null && cleanState != null && filenamePhotoAct != null
            }
            ActionManager.ActionType.DECOMMISSIONING -> {
                _idCar.value != null && idStation != null && comment != null && days != null
            }
            ActionManager.ActionType.COMMISSIONING -> {
                _idCar.value != null && idStation != null && mileage != null && fuel != null && cleanState != null
            }
            ActionManager.ActionType.START_RELOCATION -> {
                _idCar.value != null && idStation != null && comment != null
            }
            ActionManager.ActionType.END_RELOCATION -> {
                _idCar.value != null && idStation != null && mileage != null && fuel != null && cleanState != null
            }
            ActionManager.ActionType.DELIVERY -> {
                _idCar.value != null && idStation != null && reservation != null
            }
            ActionManager.ActionType.START_MAINTENANCE -> {
                _idCar.value != null && idStation != null && mileage != null && repairParams != null && contractor != null && comment != null
            }
            ActionManager.ActionType.END_MAINTENANCE -> {
                _idCar.value != null && idStation != null && mileage != null && invoiceNumber != null && price != null && comment != null
            }
            ActionManager.ActionType.TIRE_CHANGE -> {
                _idCar.value != null && tireParams != null && price != null
            }
            ActionManager.ActionType.REFILL_FUEL -> {
                _idCar.value != null && litres != null
            }
            ActionManager.ActionType.WASHING -> {
                _idCar.value != null && idCarWash != null && price != null
            }
            else -> false
        }
    }

    private fun isLastScreen(): Boolean {
        currentScreen?.let {
            screensList?.let { screens ->
                val curIndex = screens.indexOf(currentScreen)
                return curIndex == screens.size - 1
            }
        }

        return false
    }

    private fun actionSendSuccess(messageRes: Int) {
        prevActionSendMessage = messageRes
        _state.value = CarActionState.Success(messageRes)
        setExtraAction()
    }

    private fun pickup() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading
            val result = actionUseCases.pickUpUseCases(
                Action.Pickup(
                    reservation = reservation ?: return@launch,
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStationOpenedShift ?: return@launch,
                    carParam = CarParam(
                        mileage = mileage ?: return@launch,
                        fuel = fuel ?: return@launch,
                        cleanState = cleanState ?: return@launch
                    ),
                    actPhotoFileName = filenamePhotoAct ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.pickup_information_sent)
                    //_state.value = CarActionState.Success(R.string.pickup_information_sent)
                    //setExtraAction()
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun dropOff() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading
            val result = actionUseCases.dropOffUseCase(
                Action.DropOff(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStationOpenedShift ?: return@launch,
                    carParam = CarParam(
                        mileage = mileage ?: return@launch,
                        fuel = fuel ?: return@launch,
                        cleanState = cleanState ?: return@launch
                    ),
                    actPhotoFileName = filenamePhotoAct ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    if (_extraAction.value == START_RELOCATION_ACTION) {
                        action = _extraAction.value
                        clearExtraAction()
                        sendAction()
                    } else {
                        actionSendSuccess(R.string.drop_off_information_sent)
                    }
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun commissioning() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.commissioningUseCase(
                Action.Commissioning(
                    idCar = _idCar.value ?: return@launch,
                    licensePlate = licensePlate ?: return@launch,
                    idStation = idStation ?: return@launch,
                    carParam = CarParam(
                        mileage = mileage ?: return@launch,
                        fuel = fuel ?: return@launch,
                        cleanState = cleanState!!,
                    ),
                    comment = comment ?: ""
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.commissioning_information_sent)
                    /*_state.value = CarActionState.Success(R.string.commissioning_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun decommissioning() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.decommissioningUseCase(
                Action.Decommissioning(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    comment = comment ?: return@launch,
                    daysStop = days ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.decommissioning_information_sent)
                    /*_state.value = CarActionState.Success(R.string.decommissioning_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun startRelocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.relocationStartUseCase(
                Action.Relocation.Start(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    comment = comment ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    var message = R.string.start_relocation_information_sent

                    dropOffPlace?.let { place ->
                        if (place == DropOffPlace.ON_ADDRESS) {
                            message = R.string.drop_off_and_start_relocation_information_sent
                        }
                    }

                    actionSendSuccess(message)
                    /*_state.value = CarActionState.Success(message)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun endRelocation() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.relocationEndUseCase(
                Action.Relocation.End(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    carParam = CarParam(
                        mileage = mileage ?: return@launch,
                        fuel = fuel ?: return@launch,
                        cleanState = cleanState ?: return@launch,
                    )
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.end_relocation_information_sent)
                    /*_state.value = CarActionState.Success(R.string.end_relocation_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun deliveryCar() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.deliveryUseCase(
                Action.Delivery(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    reservation = reservation ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.car_delivery_information_sent)
                    /*_state.value = CarActionState.Success(R.string.car_delivery_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun startMaintenance() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading
            val result = actionUseCases.maintenanceStartUseCase(
                Action.Maintenance.Start(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    mileage = mileage ?: return@launch,
                    params = repairParams ?: return@launch,
                    contractor = contractor ?: return@launch,
                    comment = comment ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.start_maintenance_information_sent)
                    /*_state.value = CarActionState.Success(R.string.start_maintenance_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun endMaintenance() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.maintenanceEndUseCase(
                Action.Maintenance.End(
                    idCar = _idCar.value ?: return@launch,
                    idStation = idStation ?: return@launch,
                    mileage = mileage ?: return@launch,
                    invoiceNumber = invoiceNumber ?: return@launch,
                    price = price ?: return@launch,
                    comment = comment ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.end_maintenance_information_sent)
                    /*_state.value = CarActionState.Success(R.string.end_maintenance_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun tireChange() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.changeTiresUseCase(
                Action.ChangeTires(
                    idCar = _idCar.value ?: return@launch,
                    tireParams = tireParams ?: return@launch,
                    price = price ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.tire_change_information_sent)
                    /*_state.value = CarActionState.Success(R.string.tire_change_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun refillFuel() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.refillFuelUseCase(
                Action.RefillFuel(
                    idCar = _idCar.value ?: return@launch,
                    litres = litres ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.refueling_information_sent)
                    /*_state.value = CarActionState.Success(R.string.refueling_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun washing() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = CarActionState.Loading

            val result = actionUseCases.washingUseCase(
                Action.Washing(
                    idCar = _idCar.value ?: return@launch,
                    idCarWash = idCarWash ?: return@launch,
                    price = price ?: return@launch
                )
            )

            when (result) {
                is Result.Success -> {
                    actionSendSuccess(R.string.car_wash_information_sent)
                    /*_state.value = CarActionState.Success(R.string.car_wash_information_sent)
                    setExtraAction()*/
                }
                is Result.Error -> {
                    _state.value = CarActionState.Error(getErrorByCode(result.errorCode))
                }
            }
        }
    }

    private fun setExtraAction() {
        action?.let { currentAction ->
            clearExtraAction()

            val availableAction = ActionManager.getAvailableExtraAction(currentAction)
            Log.d(TAG, "extra action is $extraAction")
            if (availableAction != null) {

                _extraAction.value = availableAction
                val screens = getScreensExtraAction()

                if (screens != null) {
                    screensList = screens
                }
            }
        }
    }

    fun selectExtraAction() {
        _extraAction.value?.let { actionExtra ->
            prevAction = action
            action = actionExtra
            comment = null
            val screens = screensList ?: return
            updateCurrentScreen(screens[0])
            clearState()
        }
    }

    private fun getScreensExtraAction(): List<ActionParamScreenManager.OptionScreen>? {
        _extraAction.value?.let { action ->
            return removeEnteredScreens(
                ActionParamScreenManager.getActionScreens(action)
            )
        }

        return null
    }

    private fun removeEnteredScreens(_screens: List<ActionParamScreenManager.OptionScreen>?): List<ActionParamScreenManager.OptionScreen>? {
        if (_screens == null) {
            return null
        }

        val enteredScreens = screensList ?: return null

        val screens = _screens.toMutableList()

        val iterator = screens.iterator()
        while (iterator.hasNext()) {
            val screen = iterator.next()
            for (enteredScreen in enteredScreens) {
                if (screen == enteredScreen && screen != SCREEN_SEND_ACTION && screen != SCREEN_COMMENT) {
                    iterator.remove()
                    break
                }
            }
        }

        return screens.toList()
    }

    private fun getActionScreens(): List<ActionParamScreenManager.OptionScreen>? {
        return ActionParamScreenManager.getActionScreens(action ?: return null)
    }

    private fun getNewActionScreens(idStatus: Int): List<ActionParamScreenManager.OptionScreen>? {
        return ActionParamScreenManager.getActionScreens(idStatus ?: return null)
    }

    private fun getScreenId(screen: ActionParamScreenManager.OptionScreen) =
        ActionParamScreenManager.getScreenResource(screen)

    private fun getScreenEnum(idScreen: Int) = ActionParamScreenManager.getScreenEnum(idScreen)


    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> R.string.check_login_and_pass
            else -> R.string.error_load_data
        }

    sealed class CarActionState {
        data class Success(val messageRes: Int) : CarActionState()
        data class Error(val errorRes: Int) : CarActionState()
        object Loading : CarActionState()
        object Empty : CarActionState()
    }
}