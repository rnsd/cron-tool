package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.expression.ValueExpression

data class CronAttribute(val expression: String, val errorMessage: String?) {

    companion object {
        fun <T : CronUnit> from(
            cronFieldExpression: String,
            expressionEvaluationResult: Result<ValueExpression<T>>
        ): CronAttribute {
            return CronAttribute(cronFieldExpression, expressionEvaluationResult.fold({ null }, { it.message }))
        }
    }
}

data class CronEvaluationResult(
    val minute: CronAttribute,
    val hour: CronAttribute,
    val dayOfMonth: CronAttribute,
    val month: CronAttribute,
    val dayOfWeek: CronAttribute,
    val scheduleExplanation: String?
)