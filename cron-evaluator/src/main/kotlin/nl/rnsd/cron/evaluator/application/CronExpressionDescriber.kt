package nl.rnsd.cron.evaluator.application

import java.util.Locale
import nl.rnsd.cron.evaluator.model.CronExpression
import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.SingularValueExpression
import nl.rnsd.cron.evaluator.model.ValueExpression
import nl.rnsd.cron.evaluator.model.util.ExpressionUtils.padNumericToTwoDigits
import org.springframework.stereotype.Service

@Service
class CronExpressionDescriber {
    private val space = " "

    fun explain(cronExpression: CronExpression): String {
        val stringBuilder = StringBuilder()

        //hour and minute
        stringBuilder.append(getHourMinuteDescription(cronExpression)).append(space)

        //day-of-week and day-of-month
        dayOfWeekAndMonth(cronExpression, stringBuilder)

        //month
        cronExpression.month.describe()?.let { stringBuilder.append(it) }

        return stringBuilder.toString().trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + "."
    }

    private fun dayOfWeekAndMonth(
        cronExpression: CronExpression,
        stringBuilder: StringBuilder
    ) {
        cronExpression.dayOfMonth.describe()?.let { stringBuilder.append(it).append(space) }

        //optional "and " if both dayOfMonth and dayOfWeek have value
        val dayOfMonthDescription: String? = cronExpression.dayOfMonth.describe()
        val dayOfWeekDescription: String? = cronExpression.dayOfWeek.describe()
        dayOfMonthDescription?.let { _ ->
            dayOfWeekDescription?.let { _ ->
                stringBuilder.append("and ")
            }
        }

        //day-of-week
        dayOfWeekDescription?.let { stringBuilder.append(it).append(space) }
    }

    private fun getHourMinuteDescription(cronExpression: CronExpression): String {
        val minuteExpression = cronExpression.minute
        val hourExpression = cronExpression.hour

        val descriptions = getExactDateTime(minuteExpression, hourExpression).let { exactDateTime ->
            if (exactDateTime != null) listOf(exactDateTime)
            else listOf(minuteExpression.describe(), hourExpression.describe())
        }

        return descriptions
            .filterNotNull()
            .filter { it.isNotBlank() }
            .joinToString(space)
    }

    private fun getExactDateTime(minuteExpr: ValueExpression<Minute>, hourExpr: ValueExpression<Hour>): String? =
        getNumericValue(minuteExpr)?.let { minute ->
            getNumericValue(hourExpr)?.let { hour ->
                "At ${padNumericToTwoDigits(hour)}:${padNumericToTwoDigits(minute)}"
            }
        }

    private fun getNumericValue(expression: ValueExpression<out CronUnit>): Numeric? =
        (expression as? SingularValueExpression)?.cronUnit?.value.takeIf { it is Numeric } as? Numeric
}