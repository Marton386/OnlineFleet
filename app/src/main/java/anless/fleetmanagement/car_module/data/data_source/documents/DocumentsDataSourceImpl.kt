package anless.fleetmanagement.car_module.data.data_source.documents

import anless.fleetmanagement.car_module.data.api.DocumentsApi
import anless.fleetmanagement.car_module.domain.model.SavedPhotoInfo
import anless.fleetmanagement.core.utils.ErrorCodes
import anless.fleetmanagement.core.utils.RetrofitParseException
import anless.fleetmanagement.core.utils.data_result.Result
import anless.fleetmanagement.user_module.domain.model.SourceInfo
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.UnknownHostException

class DocumentsDataSourceImpl(
    private val documentsApi: DocumentsApi
) : DocumentsDataSource {

    companion object {
        private val contentTypeMultipart = "multipart/form-data".toMediaTypeOrNull()
    }

    override suspend fun saveRentAct(
        sourceInfo: SourceInfo,
        imageByteArray: ByteArray
    ): Result<SavedPhotoInfo> =
        try {
            //val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageByteArray)
            //val reqFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), imageByteArray)
            val reqFile = imageByteArray.toRequestBody(contentTypeMultipart)
            val formData = MultipartBody.Part.createFormData("file", "photo.jpg", reqFile)
            val response = documentsApi.saveRentAct(
                hash = sourceInfo.userHash.hash.toRequestBody(contentTypeMultipart),
                image = formData,
                appVersion = sourceInfo.appVersionCode.toString()
                    .toRequestBody(contentTypeMultipart)
            )

            if (response.isSuccessful) {
                val filename = response.body()?.filename ?: throw RetrofitParseException()
                Result.Success(SavedPhotoInfo(filename))
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Save rent act exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun saveMaintenancePhotos(
        sourceInfo: SourceInfo,
        imagesByteArray: List<ByteArray>
    ): Result<SavedPhotoInfo> =
        try {
            val formDataList: MutableList<MultipartBody.Part> = mutableListOf()

            for (i in imagesByteArray.indices) {
                val formData = MultipartBody.Part.createFormData(
                    "file_damage${i}",
                    "photo.jpg",
                    imagesByteArray[i].toRequestBody(contentTypeMultipart)
                )
                formDataList.add(formData)
            }

            val response = documentsApi.saveMaintenancePhotos(
                hash = sourceInfo.userHash.hash.toRequestBody(contentTypeMultipart),
                images = formDataList.toList(),
                appVersion = sourceInfo.appVersionCode.toString()
                    .toRequestBody(contentTypeMultipart)
            )

            if (response.isSuccessful) {
                val photoFilenames = response.body()?.filenames ?: throw RetrofitParseException()
                Result.Success(SavedPhotoInfo(photoFilenames))
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Save maintenance photos exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun downloadInsurance(
        sourceInfo: SourceInfo,
        filename: String
    ): Result<ByteArray> =
        try {
            val url =
                anless.fleetmanagement.car_module.data.utils.Constants.INSURANCE_URL + filename

            val response = documentsApi.downloadInsurance(
                url = url
            )

            if (response.isSuccessful) {
                val byteStream = response.body()?.byteStream() ?: throw RetrofitParseException()
                val byteArray = byteStream.readBytes()
                Result.Success(byteArray)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Download insurance exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }

    override suspend fun getMinimalVersionApp(): Result<Int> =
        try {
            val response = documentsApi.getMinimalVersionApp()

            if (response.isSuccessful) {
                val minimalVersion = response.body()?.code ?: throw RetrofitParseException()
                Result.Success(minimalVersion)
            } else {
                throw HttpException(response)
            }
        } catch (e: HttpException) {
            Result.Error(e.code())
        } catch (e: UnknownHostException) {
            Result.Error(ErrorCodes.NO_INTERNET)
        } catch (e: Exception) {
            e.printStackTrace()
            Firebase.crashlytics.log(e.message ?: "Get minimal version exception without message")
            Result.Error(ErrorCodes.EXCEPTION)
        }
}