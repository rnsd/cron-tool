package nl.rnsd.cron.evaluator.model.util

import java.time.DayOfWeek
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import nl.rnsd.cron.evaluator.model.Numeric

object ExpressionUtils {

    fun getMonthName(month: Int): String {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    fun getWeekdayName(day: Int): String {
        return DayOfWeek.of(day).getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    fun addSuffix(number: Int) = when {
        number % 100 in 11..13 -> "${number}th"
        else -> when (number % 10) {
            1 -> "${number}st"
            2 -> "${number}nd"
            3 -> "${number}rd"
            else -> "${number}th"
        }
    }

    fun padNumericToTwoDigits(element: Numeric): String = element.value.toString().padStart(2, '0')
}