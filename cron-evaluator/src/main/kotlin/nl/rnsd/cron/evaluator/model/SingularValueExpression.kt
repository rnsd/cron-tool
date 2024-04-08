package nl.rnsd.cron.evaluator.model

import nl.rnsd.cron.evaluator.model.util.ExpressionUtils.getMonthName
import nl.rnsd.cron.evaluator.model.util.ExpressionUtils.getWeekdayName

data class SingularValueExpression<T : CronUnit>(val cronUnit: T) : ValueExpression<T>() {

    override fun describe(): String? =
        when (cronUnit) {
            is DayOfMonth -> createDescription({ "on day-of-month $it" }, { null })
            is DayOfWeek -> createDescription({ "on ${getWeekdayName(it)}" }, { null })
            is Month -> createDescription({ "in ${getMonthName(it)}" }, { null })
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