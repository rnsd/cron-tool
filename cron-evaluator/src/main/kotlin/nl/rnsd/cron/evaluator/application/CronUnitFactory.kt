package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.CronValue
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.Numeric

object CronUnitFactory {

    fun createMinute(value: String): Minute = createCronUnit(value) { Minute(it) }

    fun createHour(value: String): Hour = createCronUnit(value) { Hour(it) }

    fun createMonth(value: String): Month {
        return if (MonthAbbreviation.isMonthAbbreviation(value)) {
            createCronUnit(MonthAbbreviation.getMonthNr(value)) { Month(it) }
        } else {
            createCronUnit(value) { Month(it) }
        }
    }

    fun createDayOfMonth(value: String): DayOfMonth = createCronUnit(value) { DayOfMonth(it) }

    fun createDayOfWeek(value: String): DayOfWeek {
        return if (DayAbbrevation.isWeekDayAbbreviation(value)) {
            createCronUnit(DayAbbrevation.getWeekDayNr(value)) { DayOfWeek(it) }
        } else {
            createCronUnit(if (value == "0") "7" else value) { DayOfWeek(it) }
        }
    }

    private inline fun <T : CronUnit> createCronUnit(value: String, creator: (CronValue) -> T): T {
        val cronValue = CronValue.fromString(value)
        return creator(cronValue).apply { checkIntValueBoundary() }
    }

    private inline fun <T : CronUnit> createCronUnit(value: Int, creator: (CronValue) -> T): T {
        val cronValue = Numeric(value)
        return creator(cronValue).apply { checkIntValueBoundary() }
    }
}


private enum class DayAbbrevation(val abbreviation: String, val number: Int) {
    SUNDAY("SUN", 0),
    MONDAY("MON", 1),
    TUESDAY("TUE", 2),
    WEDNESDAY("WED", 3),
    THURSDAY("THU", 4),
    FRIDAY("FRI", 5),
    SATURDAY("SAT", 6);

    companion object {
        fun getWeekDayNr(abbreviation: String): Int {
            return entries.find { it.abbreviation == abbreviation }?.number
                ?: throw IllegalArgumentException("No such day exists")
        }

        fun isWeekDayAbbreviation(abbreviation: String): Boolean {
            return entries.any { it.abbreviation == abbreviation }
        }
    }
}

private enum class MonthAbbreviation(val abbreviation: String, val number: Int) {

    JANUARY("JAN", 1),
    FEBRUARI("FEB", 2),
    MARCH("MAR", 3),
    APRIL("APR", 4),
    MAY("MAY", 5),
    JUNE("JUN", 6),
    JULY("JUL", 7),
    AUGUST("AUG", 8),
    SEPTEMBER("SEP", 9),
    OCTOBER("OCT", 10),
    NOVEMBER("NOV", 11),
    DECEMBER("DEC", 12);

    companion object {
        fun getMonthNr(abbreviation: String): Int {
            return MonthAbbreviation.entries.find { it.abbreviation == abbreviation }?.number
                ?: throw IllegalArgumentException("No such month exists")
        }

        fun isMonthAbbreviation(abbreviation: String): Boolean {
            return MonthAbbreviation.entries.any { it.abbreviation == abbreviation }
        }
    }
}