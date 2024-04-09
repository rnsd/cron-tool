package nl.rnsd.cron.evaluator.model.expression

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.util.ExpressionUtils

data class SingularValueExpression<T : CronUnit>(val cronUnit: T) : ValueExpression<T>() {

    override fun describe(): String? =
        when (cronUnit) {
            is DayOfMonth -> createDescription({ "on day-of-month $it" }, { null })
            is DayOfWeek -> createDescription({ "on ${ExpressionUtils.getWeekdayName(it)}" }, { null })
            is Month -> createDescription({ "in ${ExpressionUtils.getMonthName(it)}" }, { null })
            is Hour -> createDescription({ "past hour $it" }, { null })
            is Minute -> createDescription({ "at minute $it" }, { "at every minute" })
            else -> throw IllegalArgumentException("Invalid cron unit")
        }

    private fun createDescription(supplier: (Int) -> String, elseSupplier: () -> String?): String? {
        return if (cronUnit.value is Numeric) {
            supplier((cronUnit.value as Numeric).value)
        } else {
            elseSupplier()
        }
    }

}