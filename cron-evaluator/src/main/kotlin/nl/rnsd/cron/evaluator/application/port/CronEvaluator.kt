package nl.rnsd.cron.evaluator.application.port

import nl.rnsd.cron.evaluator.application.CronEvaluationResult

interface CronEvaluator {
    fun evaluate(
        minuteStringExpression: String,
        hourStringExpression: String,
        dayOfMonthStringExpression: String,
        monthStringExpression: String,
        dayOfWeekStringExpression: String
    ): CronEvaluationResult
}