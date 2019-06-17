package com.davlop.skifun.utils

import android.content.Context
import com.davlop.skifun.R
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.util.*

object DateTimeUtils {

    private const val formatter = "yyyy-MM-dd HH:mm:ss"

    // true if a given utc string represents today's date
    fun checkIfStringIsToday(date: String): Boolean =
            areTheSameDay(getUserDateTimeFromString(date), getTodaysDateTime())

    // true if a given utc string represents tomorrow's date
    fun checkIfStringIsTomorrow(date: String): Boolean {
        val tomorrow = getTodaysDateTime().plusDays(1)
        return areTheSameDay(tomorrow, getUserDateTimeFromString(date))
    }

    // true if a given utc string represents an important time of the day (
    // (morning ~8am | evening ~3pm | night ~9pm)
    fun checkIfStringIsImportantTime(date: String): Boolean {
        val dateAsDateTime = getUserDateTimeFromString(date)
        val hour = dateAsDateTime?.hourOfDay
        return hour == 7 || hour == 8 || hour == 9 || hour == 14 || hour == 15 || hour == 16
                || hour == 20 || hour == 21 || hour == 22
    }

    fun getNowFormatted(): String = getTodaysDateTime().toString(formatter)

    // converts a given utc string into a displayable string suitable for Ui
    // return example: (Tomorrow at 3:00)
    fun convertStringToDisplayableString(date: String, context: Context): String {
        val displayableString = StringBuilder()

        if (checkIfStringIsToday(date)) {
            displayableString.append(context.resources.getString(R.string.time_today))
            displayableString.append(getTimeFromString(date))
        } else if (checkIfStringIsTomorrow(date)) {
            displayableString.append(context.resources.getString(R.string.time_tomorrow))
            displayableString.append(getTimeFromString(date))
        } else {
            val dateTime = getUserDateTimeFromString(date)
            dateTime?.let {
                val monthString = context.resources.getStringArray(R.array.months)[dateTime.monthOfYear]
                val dayString = Integer.toString(dateTime.dayOfMonth)
                displayableString.append("$dayString $monthString")
                displayableString.append(" " + getDayTimeFromString(dateTime.hourOfDay, context))
            }
        }

        return displayableString.toString()
    }

    // extracts the time from a given utc string
    private fun getTimeFromString(date: String): String {
        val userDateTime = getUserDateTimeFromString(date) ?: return ""

        val hour = Integer.toString(userDateTime.hourOfDay)
        val minute = userDateTime.minuteOfHour

        // if necessary, add another 0 to convert nn:0 into nn:00
        if (minute == 0) return " $hour:00"
        else return " $hour:$minute"
    }

    // returns either Morning, Afternoon, or Night based on a given time
    private fun getDayTimeFromString(hour: Int, context: Context): String {
        return when (hour) {
            in 5..12 -> context.resources.getStringArray(R.array.day_times)[0]
            in 13..19 -> context.resources.getStringArray(R.array.day_times)[1]
            in 20..23 -> context.resources.getStringArray(R.array.day_times)[2]
            in 0..4 -> context.resources.getStringArray(R.array.day_times)[2]
            else -> ""
        }
    }

    private fun getTodaysDateTime(): DateTime {
        val userTimeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
        return DateTime.now(userTimeZone)
    }

    // returns a DateTime object on user's time zone from a utc string
    private fun getUserDateTimeFromString(dateTime: String): DateTime? {
        try {
            val utcFormatter = DateTimeFormat.forPattern(formatter).withZoneUTC()
            val userTimeZone = DateTimeZone.forTimeZone(TimeZone.getDefault())
            return DateTime.parse(dateTime, utcFormatter).withZone(userTimeZone)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        } catch (ex: UnsupportedOperationException) {
            ex.printStackTrace()
        }

        return null
    }

    private fun areTheSameDay(dt1: DateTime?, dt2: DateTime?): Boolean {
        if (dt1 != null && dt2 != null) {
            return dt1.year == dt2.year && dt1.monthOfYear == dt2.monthOfYear && dt1.dayOfMonth ==
                    dt2.dayOfMonth
        }

        return false
    }

}