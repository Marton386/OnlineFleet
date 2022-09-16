package anless.fleetmanagement.car_module.presentation.ui.act_photo

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.domain.usecase.documents.SavePhotoActUseCase
import anless.fleetmanagement.car_module.presentation.ui.maitenance.MaintenanceViewModel
import anless.fleetmanagement.car_module.presentation.utils.CameraUtility
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.presentation.ui.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActPhotoViewModel @Inject constructor(
    private val savePhotoActUseCase: SavePhotoActUseCase,
    private val cameraUtility: CameraUtility
) : ViewModel() {

    companion object{
        const val TAG = "ActPhotoViewModel"
    }

    private var photoUri: Uri? = null
    private val _photoAct = MutableStateFlow<Bitmap?>(value = null)
    val photoAct = _photoAct.asStateFlow()

    private val _photoActThumbnail = MutableStateFlow<Bitmap?>(value = null)
    val photoActThumbnail = _photoActThumbnail.asStateFlow()

    private val _state = MutableStateFlow<ActPhotoState>(ActPhotoState.Empty)
    val state = _state.asStateFlow()

    fun setPhotoActUri(uri: Uri) {
        photoUri = uri
    }

 /*   fun setPhotoAct(photo: Bitmap) {
        _photoAct.value = photo
        _photoActThumbnail.value = cameraUtility.getThumbnail(photo)
    }*/

    fun hasCameraPermissions() = cameraUtility.hasCameraPermissions()

    fun setPhotoAct(uri: Uri) {
        val bitmap = cameraUtility.compressForServerByUri(uri)
        _photoAct.value = bitmap
        _photoActThumbnail.value = cameraUtility.getThumbnail(bitmap)
    }

    fun createImageFileUri() = cameraUtility.createImageFileUri()

    fun getPhotoActUri() = photoUri

    fun getPhotoAct() = _photoAct.value

    fun sendPhotoAct() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(ActPhotoState.Loading)
            val imageByteArray = cameraUtility.getImageByteArray(photoAct.value ?: return@launch)
            val result = savePhotoActUseCase(imageByteArray)

            when (result) {
                is Result.Success -> {
                    _state.emit(ActPhotoState.Success(savedPhotoFilename = result.data.filename))
                }
                is Result.Error -> {
         /*           _state.emit(ActPhotoState.Error(resError = getErrorByCode(result.errorCode)))*/
                    setError(result.errorCode)
                }
            }
        }
    }

    private fun setError(codeError: Int) {
        viewModelScope.launch {
            getErrorByCode(codeError)?.let { resError ->
                _state.emit(ActPhotoState.Error(resError))
            }
        }
    }

    private fun getErrorByCode(errorCode: Int) =
        when (errorCode) {
            ErrorCodes.NO_INTERNET -> R.string.check_internet_connection
            ErrorCodes.SESSION_IS_CLOSED -> null//R.string.your_session_is_closed
            else -> R.string.error_load_data
        }

    sealed class ActPhotoState {
        data class Success(val savedPhotoFilename: String): ActPhotoState()
        data class Error(val resError: Int): ActPhotoState()
        object Loading: ActPhotoState()
        object Empty: ActPhotoState()
    }
}