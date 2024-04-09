package nl.rnsd.cron.evaluator.model.expression

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.WildCard
import nl.rnsd.cron.evaluator.model.util.ExpressionUtils

sealed class StepExpression<T : CronUnit> : ValueExpression<T>() {

    protected fun validateStep(value: CronUnit) {
        require(value.value !is WildCard) { "Invalid step expression: step value cannot be wildcard" }
        val stepValue: Int = (value.value as Numeric).value
        require(stepValue != 0) { "Invalid step expression: step value must be > 0" }
    }

    data class WildCardStep<T : CronUnit>(val value: T) : StepExpression<T>() {
        init {
            validateStep(value)
        }

        override fun describe(): String = stepDescription(value)
    }

    data class RangeStep<T : CronUnit>(val rangeExpression: RangeExpression<T>, val value: T) : StepExpression<T>() {
        init {
            validateStep(value)
        }

        override fun describe(): String = "${stepDescription(value)} ${rangeDescription(rangeExpression)}"
    }

    private fun stepValueDescription(value: T): String {
        val stepValue =
            value.value as? Numeric ?: throw IllegalArgumentException("Step value must be numeric")
        return if (stepValue.value == 1) value.unitDescription
        else "${ExpressionUtils.addSuffix(stepValue.value)} ${value.unitDescription}"
    }

    fun stepDescription(value: T): String {
        return when (value) {
            is Minute -> "at every ${stepValueDescription(value)}"
            is Hour -> "past every ${stepValueDescription(value)}"
            is DayOfMonth -> "on every ${stepValueDescription(value)}"
            is Month -> "in every ${stepValueDescription(value)}"
            is DayOfWeek -> "on every ${stepValueDescription(value)}"
            else -> error("Invalid step value type")
        }
    }

    fun rangeDescription(rangeExpression: RangeExpression<T>): String {
        val start = rangeExpression.startValue()
        val end = rangeExpression.endValue()
        return when (rangeExpression.start) {
            is Minute,
            is Hour,
            is DayOfMonth -> "from $start through $end"

            is Month -> "from ${ExpressionUtils.getMonthName(start)} through ${ExpressionUtils.getMonthName(end)}"
            is DayOfWeek -> "from ${ExpressionUtils.getWeekdayName(start)} through ${ExpressionUtils.getWeekdayName(end)}"
            else -> error("Invalid range value type")
        }
    }

}