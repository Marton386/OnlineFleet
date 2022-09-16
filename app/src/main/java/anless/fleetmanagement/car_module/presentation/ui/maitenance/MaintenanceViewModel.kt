package anless.fleetmanagement.car_module.presentation.ui.maitenance

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.model.RepairParams
import anless.fleetmanagement.car_module.domain.usecase.documents.SaveRepairPhotosUseCase
import anless.fleetmanagement.car_module.presentation.utils.CameraUtility
import anless.fleetmanagement.car_module.presentation.utils.MaintenanceType
import anless.fleetmanagement.car_module.presentation.utils.RepairType
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val saveRepairPhotosUseCase: SaveRepairPhotosUseCase,
    private val cameraUtility: CameraUtility
) : ViewModel() {

    companion object {
        const val MAX_AMOUNT_PHOTOS = 10
    }

    private val _typeWorks = MutableStateFlow<RepairType?>(value = null)
    val typeWorks = _typeWorks.asStateFlow()

    private val _typeMaintenance = MutableStateFlow<MaintenanceType?>(value = null)

    private var photoUri: Uri? = null

    private val _uriPhotosMaintenance = MutableStateFlow<List<PhotoItem>?>(value = null)
    val uriPhotosMaintenance = _uriPhotosMaintenance.asStateFlow()
    private var uriPhotosList: MutableList<PhotoItem> = mutableListOf()

    private val _maintenanceTypes = MutableStateFlow(value = initMaintenanceTypes())
    val maintenanceTypes = _maintenanceTypes.asStateFlow()

    private val _state = MutableSharedFlow<MaintenanceState>()
    val state = _state.asSharedFlow()

    val readyMaintenanceParams: StateFlow<Boolean> =
        combine(
            _typeWorks,
            _typeMaintenance,
            _uriPhotosMaintenance
        ) { typeWorks, typeMaintenance, photos ->
            when (typeWorks) {
                RepairType.BODY_WORKS -> !photos.isNullOrEmpty()
                RepairType.LOCKSMITH_WORKS -> true
                RepairType.MAINTENANCE -> typeMaintenance != null
                else -> false
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = false)


    fun hasCameraPermissions() = cameraUtility.hasCameraPermissions()

    fun createImageFileUri() = cameraUtility.createImageFileUri()

    fun isMaximumPhotosLoaded() = uriPhotosList.size == MAX_AMOUNT_PHOTOS

    fun getPhotosMaxAmount() = MAX_AMOUNT_PHOTOS

    fun isSomePhotosLoaded() = uriPhotosList.size > 0

    fun setTypeWorks(type: RepairType) {
        _typeWorks.value = type
    }

    fun setPhotoUri(uri: Uri) {
        photoUri = uri
    }

    fun getPhotoUri() = photoUri

    fun addPhotos(uriList: List<Uri>) {
        viewModelScope.launch(Dispatchers.IO) {
            uriList.forEach { item ->
                if (uriPhotosList.size >= MAX_AMOUNT_PHOTOS) {
                    return@forEach
                }

                val uriPhoto = PhotoItem(item)
                if (!isDuplicate(uriPhoto)) {
                    uriPhotosList.add(uriPhoto)
                }
            }

            copyPhotosFromList()
        }
    }

    private fun isDuplicate(photoItem: PhotoItem) =
        uriPhotosList.contains(photoItem)

    private fun initMaintenanceTypes(): List<MaintenanceItem> {
        val maintenanceTypeListId = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        return listOf(
            MaintenanceItem(id = maintenanceTypeListId[0], type = MaintenanceType.TO_1),
            MaintenanceItem(id = maintenanceTypeListId[1], type = MaintenanceType.TO_2),
            MaintenanceItem(id = maintenanceTypeListId[2], type = MaintenanceType.TO_3),
            MaintenanceItem(id = maintenanceTypeListId[3], type = MaintenanceType.TO_4),
            MaintenanceItem(id = maintenanceTypeListId[4], type = MaintenanceType.TO_5),
            MaintenanceItem(id = maintenanceTypeListId[5], type = MaintenanceType.TO_6),
            MaintenanceItem(id = maintenanceTypeListId[6], type = MaintenanceType.TO_7),
            MaintenanceItem(id = maintenanceTypeListId[7], type = MaintenanceType.TO_8),
            MaintenanceItem(id = maintenanceTypeListId[8], type = MaintenanceType.TO_9),
            MaintenanceItem(id = maintenanceTypeListId[9], type = MaintenanceType.TO_10),
            MaintenanceItem(id = maintenanceTypeListId[10], type = MaintenanceType.TO_11),
            MaintenanceItem(id = maintenanceTypeListId[11], type = MaintenanceType.TO_12),
            MaintenanceItem(id = maintenanceTypeListId[12], type = MaintenanceType.TO_13),
            MaintenanceItem(id = maintenanceTypeListId[13], type = MaintenanceType.TO_14),
            MaintenanceItem(id = maintenanceTypeListId[14], type = MaintenanceType.TO_15),
        )
    }

    fun deletePhotoFromList(photoItem: PhotoItem) {
        uriPhotosList.remove(photoItem)

        copyPhotosFromList()
    }

    fun collectMaintenanceParams() {
        viewModelScope.launch {
            when (_typeWorks.value) {
                RepairType.BODY_WORKS -> {
                    uploadRepairPhotos()
                }
                RepairType.LOCKSMITH_WORKS -> {
                    if (uriPhotosList.isEmpty()) {
                        _state.emit(
                            MaintenanceState.Success(
                                RepairParams(
                                    typeRepair = _typeWorks.value ?: return@launch,
                                    typeTO = null,
                                    repairPhotosFilename = null
                                )
                            )
                        )
                    } else {
                        uploadRepairPhotos()
                    }
                }

                RepairType.MAINTENANCE -> {
                    _state.emit(
                        MaintenanceState.Success(
                            RepairParams(
                                typeRepair = _typeWorks.value ?: return@launch,
                                typeTO = _typeMaintenance.value,
                                repairPhotosFilename = null
                            )
                        )
                    )
                }
                else -> {}
            }
        }
    }

    private fun uploadRepairPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(MaintenanceState.Loading)

            val imagesByteArrayList = mutableListOf<ByteArray>()
            uriPhotosList.forEach { photoItem ->
                val imageByteArray = cameraUtility.getByteArrayByUri(photoItem.uri)
                imagesByteArrayList.add(imageByteArray)
            }

            val result = saveRepairPhotosUseCase(imagesByteArrayList)

            when (result) {
                is Result.Success -> {
                    _state.emit(
                        MaintenanceState.Success(
                            RepairParams(
                                typeRepair = _typeWorks.value ?: return@launch,
                                typeTO = _typeMaintenance.value,
                                repairPhotosFilename = result.data.filename
                            )
                        )
                    )
                }
                is Result.Error -> {
                    setError(result.errorCode)
                }
            }
        }
    }

    private fun setError(codeError: Int) {
        viewModelScope.launch {
            getErrorByCode(codeError)?.let { resError ->
                _state.emit(MaintenanceState.Error(resError))
            }
        }
    }

    private fun copyPhotosFromList() {
        _uriPhotosMaintenance.value = uriPhotosList.toList()
    }


    fun setMaintenanceType(type: MaintenanceType) {
        _typeMaintenance.value = type
    }

    fun getMaintenanceType() = _typeMaintenance.value

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null // R.string.your_session_is_closed
            else -> R.string.error_load_data
        }

    sealed class MaintenanceState {
        data class Success(val maintenanceParams: RepairParams) :
            MaintenanceState()

        data class Error(val resError: Int) : MaintenanceState()
        object Loading : MaintenanceState()
        object Empty : MaintenanceState()
    }
}