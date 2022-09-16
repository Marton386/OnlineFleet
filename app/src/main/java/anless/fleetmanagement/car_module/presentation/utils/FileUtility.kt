package anless.fleetmanagement.car_module.presentation.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import anless.fleetmanagement.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class FileUtility @Inject constructor(
    val context: Context
) {
    companion object {
        private const val TAG = "FileUtility"
    }

    object FileType {
        const val PDF = ".pdf"
        const val JPG = ".jpg"
        const val PNG = ".png"
    }

    fun checkFileType(filename: String): String? {
        if (filename.lowercase().endsWith(FileType.JPG) || filename.lowercase().endsWith(".jpeg")) {
            return FileType.JPG
        }

        if (filename.lowercase().endsWith(FileType.PNG)) {
            return FileType.PNG
        }

        if (filename.lowercase().endsWith(FileType.PDF)) {
            return FileType.PDF
        }

        return null
    }

    fun saveFile(filename: String, byteArray: ByteArray): Uri? {
        val suffix = checkFileType(filename) ?: return null

        val prefix = filename.removeSuffix(suffix) + "_"

        try {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = File.createTempFile(
                prefix,
                suffix,
                storageDir
            )

            val outputStream = FileOutputStream(file)
            outputStream.write(byteArray)
            outputStream.close()

            return FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.file_provider",
                file
            )

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}