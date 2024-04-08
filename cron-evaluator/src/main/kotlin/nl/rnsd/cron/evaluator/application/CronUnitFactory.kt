package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.CronValue
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month

object CronUnitFactory {
    private inline fun <reified T : CronUnit> createCronUnit(value: String, creator: (CronValue) -> T): T {
        val cronValue = CronValue.fromString(value)
        return creator(cronValue).apply { checkIntValueBoundary() }
    }

    fun createMinute(value: String): Minute = createCronUnit(value) { Minute(it) }
    fun createHour(value: String): Hour = createCronUnit(value) { Hour(it) }
    fun createMonth(value: String): Month = createCronUnit(value) { Month(it) } //todo handle JAN-DEC
    fun createDayOfMonth(value: String): DayOfMonth = createCronUnit(value) { DayOfMonth(it) }
    fun createDayOfWeek(value: String): DayOfWeek =
        createCronUnit(if (value == "0") "7" else value) { DayOfWeek(it) } //todo handle SUN-SAT
}