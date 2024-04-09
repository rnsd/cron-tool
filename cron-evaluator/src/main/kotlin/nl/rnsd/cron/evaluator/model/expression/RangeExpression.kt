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

data class RangeExpression<T : CronUnit>(val start: T, val end: T) : ValueExpression<T>() {
    init {
        validateRange(start, end)
    }

    private fun validateRange(start: T, end: T) {
        require(start.value !is WildCard) { "Range start value cannot be wildcard" }
        require(end.value !is WildCard) { "Range expression: range end value cannot be wildcard" }
        require((start.value as Numeric).value < (end.value as Numeric).value) { "Start of range >= end of range" }
    }

    fun startValue(): Int = (start.value as Numeric).value
    fun endValue(): Int = (end.value as Numeric).value

    override fun describe(): String = when (start) {
        is DayOfMonth -> createDescription { start, end -> "on every day-of-month from $start through $end" }

        is DayOfWeek -> createDescription { start, end ->
            "on every day-of-week from ${ExpressionUtils.getWeekdayName(start)} through ${
                ExpressionUtils.getWeekdayName(
                    end
                )
            }"
        }

        is Month -> createDescription { start, end ->
            "in every month from ${ExpressionUtils.getMonthName(start)} through ${ExpressionUtils.getMonthName(end)}"
        }

        is Hour -> createDescription { start, end -> "past every hour from $start through $end" }
        is Minute -> createDescription { start, end -> "at every minute from $start through $end" }
        else -> throw IllegalArgumentException("Invalid cron unit")
    }

    private fun createDescription(supplier: (Int, Int) -> String) = supplier(startValue(), endValue())

}