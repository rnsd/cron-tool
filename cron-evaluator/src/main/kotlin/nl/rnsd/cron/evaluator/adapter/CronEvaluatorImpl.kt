package nl.rnsd.cron.evaluator.adapter

import nl.rnsd.cron.evaluator.application.port.CronEvaluator
import nl.rnsd.cron.evaluator.application.CronExpressionDescriber
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createDayOfMonth
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createDayOfWeek
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createHour
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createMinute
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createMonth
import nl.rnsd.cron.evaluator.application.ExpressionFactory.createValueExpression
import nl.rnsd.cron.evaluator.application.CronAttribute
import nl.rnsd.cron.evaluator.application.CronEvaluationResult
import nl.rnsd.cron.evaluator.model.CronExpression
import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.expression.ValueExpression
import org.springframework.stereotype.Service

@Service
class CronEvaluatorImpl(private val cronExpressionDescriber: CronExpressionDescriber = CronExpressionDescriber()) :
    CronEvaluator {

    override fun evaluate(
        minuteStringExpression: String,
        hourStringExpression: String,
        dayOfMonthStringExpression: String,
        monthStringExpression: String,
        dayOfWeekStringExpression: String
    ): CronEvaluationResult {

        val minuteExpression = createValueExpression(minuteStringExpression) { s -> createMinute(s) }
        val hourExpression = createValueExpression(hourStringExpression) { s -> createHour(s) }
        val dayOfMonthExpression = createValueExpression(dayOfMonthStringExpression) { s -> createDayOfMonth(s) }
        val monthExpression = createValueExpression(monthStringExpression) { s -> createMonth(s) }
        val dayOfWeekExpression = createValueExpression(dayOfWeekStringExpression) { s -> createDayOfWeek(s) }

        return CronEvaluationResult(
            minute = CronAttribute.from(minuteStringExpression, minuteExpression),
            hour = CronAttribute.from(hourStringExpression, hourExpression),
            dayOfMonth = CronAttribute.from(dayOfMonthStringExpression, dayOfMonthExpression),
            month = CronAttribute.from(monthStringExpression, monthExpression),
            dayOfWeek = CronAttribute.from(dayOfWeekStringExpression, dayOfWeekExpression),
            scheduleExplanation = getCronExplanation(
                minuteExpression = minuteExpression,
                hourExpression = hourExpression,
                dayOfMonthExpression = dayOfMonthExpression,
                monthExpression = monthExpression,
                dayOfWeekExpression = dayOfWeekExpression
            )
        )
    }

    private fun getCronExplanation(
        minuteExpression: Result<ValueExpression<Minute>>,
        hourExpression: Result<ValueExpression<Hour>>,
        dayOfMonthExpression: Result<ValueExpression<DayOfMonth>>,
        monthExpression: Result<ValueExpression<Month>>,
        dayOfWeekExpression: Result<ValueExpression<DayOfWeek>>
    ): String? {
        return runCatching {
            CronExpression(
                minuteExpression.getOrThrow(),
                hourExpression.getOrThrow(),
                dayOfMonthExpression.getOrThrow(),
                monthExpression.getOrThrow(),
                dayOfWeekExpression.getOrThrow()
            )
        }.fold(
            onSuccess = { cronExpression -> cronExpressionDescriber.describe(cronExpression) },
            onFailure = { null }
        )
    }

}