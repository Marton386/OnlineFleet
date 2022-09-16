package anless.fleetmanagement.core.utils

import android.content.Context
import anless.fleetmanagement.R
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    private const val DATE_TIME = "dd MMM yyyy HH:mm"
    private const val TIME_DATE = "HH:mm dd MMM yyyy"
    private const val DATE = "dd.MM.yyyy"
    private const val DEFAULT_TIMEZONE = "UTC"
    private const val GMT_TIMEZONE = "GMT"

    fun formatFullDateTime(seconds: Long) = getStringDate(seconds, DATE_TIME, DEFAULT_TIMEZONE)
    fun formatFullDateTimeDeviceTimezone(seconds: Long) = getStringDate(seconds, DATE_TIME, null)

    fun formatFullTimeDate(seconds: Long) = getStringDate(seconds, TIME_DATE, DEFAULT_TIMEZONE)
    fun formatDate(seconds: Long) = getStringDate(seconds, DATE, DEFAULT_TIMEZONE)


    private fun getStringDate(seconds: Long, format: String, timezoneString: String?): String {
        val timezone = if (timezoneString == null) {
            TimeZone.getDefault()
        } else {
            TimeZone.getTimeZone(timezoneString)
        }

        val locale = Locale.getDefault()
        val formatter = SimpleDateFormat(format, locale)
        val time = Calendar.getInstance(timezone)
        formatter.timeZone = timezone
        time.timeInMillis = seconds * 1000
        return formatter.format(time.time)
    }

/*    private fun getTimezone(timezone: Int?): String {
        var stringTimezone = DEFAULT_TIMEZONE
        timezone?.let { _timezone ->
            stringTimezone = GMT_TIMEZONE
            if (_timezone > 0) {
                stringTimezone += "+"
            }

            stringTimezone += _timezone
        }
        return stringTimezone
    }*/

    fun getTimeAgoString(context: Context, timeSeconds: Long): String {
        val milliseconds = 1000
        val minuteInMillis: Long = 60L * milliseconds
        val hourInMillis = 60 * minuteInMillis
        val dayInMillis = 24 * hourInMillis
        val monthInMillis = 30 * dayInMillis // ~ amount month days

        val time = timeSeconds * milliseconds
        val curTime = Calendar.getInstance().timeInMillis

        val timeAgoMillis = curTime - time

        if (timeAgoMillis >= monthInMillis) {
            val monthAmount = (timeAgoMillis / monthInMillis).toInt()
            return context.getString(
                R.string.time_ago,
                context.resources.getQuantityString(R.plurals.months, monthAmount, monthAmount)
            )
        }

        if (timeAgoMillis >= dayInMillis) {
            val dayAmount = (timeAgoMillis / dayInMillis).toInt()
            return context.getString(
                R.string.time_ago,
                context.resources.getQuantityString(R.plurals.days, dayAmount, dayAmount)
            )
        }

        if (timeAgoMillis >= hourInMillis) {
            val hourAmount = (timeAgoMillis / hourInMillis).toInt()
            return context.getString(
                R.string.time_ago,
                context.resources.getQuantityString(R.plurals.hours, hourAmount, hourAmount)
            )
        }

        if (timeAgoMillis >= minuteInMillis) {
            val minuteAmount = (timeAgoMillis / minuteInMillis).toInt()
            return context.getString(
                R.string.time_ago,
                context.resources.getQuantityString(R.plurals.minutes, minuteAmount, minuteAmount)
            )
        }

        if (timeAgoMillis < minuteInMillis) {
            return context.getString(R.string.just_now)
        }

        return context.getString(R.string.some_time)
    }
}