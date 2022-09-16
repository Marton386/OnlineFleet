package anless.fleetmanagement.car_module.presentation.utils

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import anless.fleetmanagement.BuildConfig
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CameraUtility @Inject constructor(private val context: Context) {
    companion object {
        private const val THUMBNAIL_IMAGE_SIZE = 512
        private const val HD_IMAGE_SIZE = 1920
    }

    fun hasCameraPermissions() =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.CAMERA
        )
    // Android >= Q ?

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFilename = "IMG_${timestamp}_"
        val storageDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFilename,
            ".jpg",
            storageDir
        )
    }

    fun createImageFileUri() = FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.file_provider",
        createImageFile()
    )

    fun getImageByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun getThumbnail(bitmap: Bitmap) = compressBitmap(
        bitmap = getResizedBitmap(bitmap, THUMBNAIL_IMAGE_SIZE),
        quality = 50
    )

    fun compressForServer(bitmap: Bitmap) = compressBitmap(
        bitmap = getResizedBitmap(bitmap, HD_IMAGE_SIZE),
        quality = 50
    )

    fun compressForServerByUri(uri: Uri) = compressBitmap(
        bitmap = getResizedBitmap(getBitmapByUri(uri = uri), HD_IMAGE_SIZE),
        quality = 50
    )

    fun getByteArrayByUri(uri: Uri): ByteArray {
        val bitmap = compressForServerByUri(uri)
        return getImageByteArray(bitmap)
    }

    fun getBitmapByUri(uri: Uri) =
        if (Build.VERSION.SDK_INT >= 29) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    context.contentResolver,
                    uri
                )
            )
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

    private fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val byteArray = stream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun getResizedBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}