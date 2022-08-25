package id.ac.unila.ee.himatro.ectro.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    const val DATE_ALARM_FORMAT = "yyyy-MM-dd" // 2022-08-12
    const val DATE_PICKER_FORMAT = "MMM dd, yyyy" // Aug 12, 2022
    const val DATE_DISPLAY_FORMAT = "EEEE, dd MMM yyyy" // Friday, 12 Aug 2022

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentHour(): String {
        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun getCurrentMinute(): String {
        val dateFormat = SimpleDateFormat("mm", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun mapPickerFormatToAlarmFormat(date: String): String {

        try {

            val originalFormat = SimpleDateFormat(DATE_PICKER_FORMAT, Locale.getDefault())
            val parsePicker = originalFormat.parse(date)

            val alarmFormat = SimpleDateFormat(DATE_ALARM_FORMAT, Locale.getDefault())

            return if (parsePicker != null){
                alarmFormat.format(parsePicker)
            } else {
                ""
            }
        } catch (e: Exception){
            Log.e("DateHelper", e.message.toString())
            return ""
        }

    }

    fun mapAlarmFormatToDisplayFormat(date: String): String {
        return try {
            val originalFormat = SimpleDateFormat(DATE_ALARM_FORMAT, Locale.getDefault())
            val parseAlarmDate = originalFormat.parse(date)
            val displayFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
            if (parseAlarmDate != null){
                displayFormat.format(parseAlarmDate)
            } else {
                ""
            }
        } catch (e: Exception){
            Log.e("DateHelper", e.message.toString())
            ""
        }
    }

    fun mapDisplayFormatToAlarmFormat(date: String): String {
        val originalFormat = SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.getDefault())
        val parseDisplayDate = originalFormat.parse(date)
        val alarmFormat = SimpleDateFormat(DATE_ALARM_FORMAT, Locale.getDefault())
        return if (parseDisplayDate != null){
            alarmFormat.format(parseDisplayDate)
        } else {
            ""
        }
    }

    fun getCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR)
    }
}