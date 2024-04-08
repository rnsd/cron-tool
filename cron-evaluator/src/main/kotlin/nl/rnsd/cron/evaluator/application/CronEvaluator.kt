package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.application.CronUnitFactory.createDayOfMonth
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createDayOfWeek
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createHour
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createMinute
import nl.rnsd.cron.evaluator.application.CronUnitFactory.createMonth
import nl.rnsd.cron.evaluator.application.ExpressionFactory.create
import nl.rnsd.cron.evaluator.model.CronAttribute
import nl.rnsd.cron.evaluator.model.CronEvaluationResult
import nl.rnsd.cron.evaluator.model.CronExpression
import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.ValueExpression
import org.springframework.stereotype.Service

@Service
class CronEvaluator(private val cronExpressionDescriber: CronExpressionDescriber) {

    fun evaluate(
        minute: String,
        hour: String,
        dayOfMonth: String,
        month: String,
        dayOfWeek: String
    ): CronEvaluationResult? {

        val minuteResult = resultFor { create(minute) { s -> createMinute(s) } }
        val hourResult = resultFor { create(hour) { s -> createHour(s) } }
        val dayOfMonthResult = resultFor { create(dayOfMonth) { s -> createDayOfMonth(s) } }
        val monthResult = resultFor { create(month) { s -> createMonth(s) } }
        val dayOfWeekResult = resultFor { create(dayOfWeek) { s -> createDayOfWeek(s) } }
        val explanation = getCronExplanation(minuteResult, hourResult, dayOfMonthResult, monthResult, dayOfWeekResult)

        return CronEvaluationResult.Builder()
            .minute(createCronAttribute(minute, minuteResult))
            .hour(createCronAttribute(hour, hourResult))
            .dayOfMonth(createCronAttribute(dayOfMonth, dayOfMonthResult))
            .month(createCronAttribute(month, monthResult))
            .dayOfWeek(createCronAttribute(dayOfWeek, dayOfWeekResult))
            .scheduleExplanation(explanation)
            .build()
    }

    private fun <T : CronUnit> resultFor(expression: () -> ValueExpression<T>): Result<ValueExpression<T>> {
        return try {
            Result.success(expression())
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Unknown error"))
        }
    }

    private fun <T : CronUnit> createCronAttribute(
        cronFieldExpression: String,
        expressionEvaluationResult: Result<ValueExpression<T>>
    ): CronAttribute {
        return CronAttribute(cronFieldExpression, expressionEvaluationResult.fold({ null }, { it.message }))
    }

    private fun getCronExplanation(
        minuteExpression: Result<ValueExpression<Minute>>,
        hourExpression: Result<ValueExpression<Hour>>,
        dayOfMonthExpression: Result<ValueExpression<DayOfMonth>>,
        monthExpression: Result<ValueExpression<Month>>,
        dayOfWeekExpression: Result<ValueExpression<DayOfWeek>>
    ): String? {

        if (minuteExpression.isSuccess
            && hourExpression.isSuccess
            && dayOfMonthExpression.isSuccess
            && monthExpression.isSuccess
            && dayOfWeekExpression.isSuccess
        ) {

            val cronExpression = CronExpression(
                minuteExpression.getOrThrow(),
                hourExpression.getOrThrow(),
                dayOfMonthExpression.getOrThrow(),
                monthExpression.getOrThrow(),
                dayOfWeekExpression.getOrThrow()
            )
            return cronExpressionDescriber.explain(cronExpression)
        }
        return null
    }

}