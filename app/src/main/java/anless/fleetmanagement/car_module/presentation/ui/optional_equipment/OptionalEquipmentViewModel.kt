package anless.fleetmanagement.car_module.presentation.ui.optional_equipment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.Equipment
import anless.fleetmanagement.car_module.domain.usecase.car_params.GetEquipmentUseCase
import anless.fleetmanagement.car_module.domain.utils.OptionalEquipment
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionalEquipmentViewModel @Inject constructor(
    private val getEquipmentUseCase: GetEquipmentUseCase
) : ViewModel() {

    private val _equipmentType = MutableStateFlow<OptionalEquipment?>(value = null)
    val equipmentType = _equipmentType.asStateFlow()

    private val _equipmentCode = MutableStateFlow(value = "")
    val equipmentCode = _equipmentCode.asStateFlow()

    private val _selectedEquipments =
        MutableStateFlow<List<Equipment>>(value = listOf())
    val selectedEquipments = _selectedEquipments.asStateFlow()
    private val selectedEquipmentsList: MutableList<Equipment> = mutableListOf()

    private val _state =
        MutableStateFlow<OptionalEquipmentState>(value = OptionalEquipmentState.Empty)
    val state = _state.asStateFlow()

    fun setEquipmentCode(code: String) {
        _equipmentCode.value = code
    }

    fun selectGpsNavigator() {
        _equipmentType.value = OptionalEquipment.GPS_NAVIGATOR
    }

    fun selectChildSeat() {
        _equipmentType.value = OptionalEquipment.CHILD_SEAT
    }

    fun getSelectedEquipment() = _selectedEquipments.value

    fun addEquipment() {
        when (_equipmentType.value) {
            OptionalEquipment.GPS_NAVIGATOR -> {
                addEquipmentToList(
                    Equipment(
                        code = "",
                        type = _equipmentType.value!!,
                        description = ""
                    )
                )

                clearEquipment()
            }
            OptionalEquipment.CHILD_SEAT -> {
                if (isEquipmentAddedAlready()) {
                    viewModelScope.launch {
                        _state.emit(OptionalEquipmentState.Error(resError = R.string.this_equipment_already_added))
                    }
                } else {
                    getEquipment()
                }
            }
            else -> {}
        }

        // clear all after
    }

    private fun isEquipmentAddedAlready(): Boolean {
        selectedEquipmentsList.forEach { equipment ->
            if (equipment.type == OptionalEquipment.CHILD_SEAT && equipment.code == _equipmentCode.value) {
                return true
            }
        }

        return false
    }

    private fun addEquipmentToList(equipment: Equipment) {
        selectedEquipmentsList.add(equipment)
        _selectedEquipments.value = selectedEquipmentsList.map { it }
    }

    fun deleteEquipmentFromList(equipment: Equipment) {
        selectedEquipmentsList.remove(equipment)
        _selectedEquipments.value = selectedEquipmentsList.map { it }
    }

    private fun clearEquipment() {
        _equipmentCode.value = ""
        _equipmentType.value = null
    }

    private fun getEquipment() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(OptionalEquipmentState.Loading)

            val result = getEquipmentUseCase(
                _equipmentCode.value
            )

            when (result) {
                is Result.Success -> {
                    addEquipmentToList(result.data)
                    _state.emit(OptionalEquipmentState.Success)
                    clearEquipment()
                }
                is Result.Error -> {
                    _state.emit(
                        OptionalEquipmentState.Error(
                            resError = getErrorByCode(result.errorCode)
                        )
                    )
                }
            }
        }
    }

    fun getEquipmentTitleRes(code: OptionalEquipment) =
        when (code) {
            OptionalEquipment.GPS_NAVIGATOR -> R.string.gps_navigator
            OptionalEquipment.CHILD_SEAT -> R.string.child_seat
        }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> R.string.your_session_is_closed
            ErrorCodes.NOT_FOUND -> R.string.no_equipment_with_this_number
            else -> R.string.error_load_data
        }

    sealed class OptionalEquipmentState {
        data class Error(val resError: Int) : OptionalEquipmentState()
        object Success : OptionalEquipmentState()
        object Loading : OptionalEquipmentState()
        object Empty : OptionalEquipmentState()
    }
}