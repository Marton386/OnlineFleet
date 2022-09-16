package anless.fleetmanagement.car_module.presentation.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import anless.fleetmanagement.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class ScanBarcodeContract(
    private val scanMessage: Int? = null
) : ActivityResultContract<Unit, String>() {

    companion object {
        private const val DEFAULT_MESSAGE = R.string.scan_code
    }

    override fun createIntent(context: Context, input: Unit): Intent = ScanOptions()
        .setBeepEnabled(false)
        .setCameraId(0)
        .setPrompt(context.getString(scanMessage ?: DEFAULT_MESSAGE))
        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        .setCaptureActivity(anless.fleetmanagement.car_module.presentation.utils.CaptureActivityPortrait::class.java)
        .setBarcodeImageEnabled(true)
        .createScanIntent(context)


    override fun parseResult(resultCode: Int, intent: Intent?): String {
        val scanResult = ScanContract().parseResult(resultCode, intent)
        scanResult?.let { result ->
            return result.contents ?: ""
        }
        return ""
    }
}

