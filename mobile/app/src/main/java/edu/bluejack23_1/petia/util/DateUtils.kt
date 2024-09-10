package edu.bluejack23_1.petia.util

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        fun formatTimeAgo(dateString: String): String {
            val dateFormatPattern = "yyyy-MM-dd HH:mm:ss"
            val dateFormat = SimpleDateFormat(dateFormatPattern, Locale.getDefault())

            try {
                val date = dateFormat.parse(dateString)
                val prettyTime = PrettyTime(Locale.getDefault())
                return prettyTime.format(date)
            } catch (e: Exception) {
                return "Invalid Date"
            }
        }

        fun formatDateAsDayMonthYear(dateString: String): String {
            val inputDateFormatPattern = "yyyy-MM-dd HH:mm:ss"
            val outputDateFormatPattern = "dd MMM yyyy"

            val inputDateFormat = SimpleDateFormat(inputDateFormatPattern, Locale.getDefault())
            val outputDateFormat = SimpleDateFormat(outputDateFormatPattern, Locale.getDefault())

            try {
                val date = inputDateFormat.parse(dateString)
                return outputDateFormat.format(date)
            } catch (e: Exception) {
                return "Invalid Date"
            }
        }
    }
}
