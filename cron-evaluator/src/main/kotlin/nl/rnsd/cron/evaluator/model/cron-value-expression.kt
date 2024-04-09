package nl.rnsd.cron.evaluator.model

import nl.rnsd.cron.evaluator.model.expression.ValueExpression

data class CronExpression(
    val minute: ValueExpression<Minute>,
    val hour: ValueExpression<Hour>,
    val dayOfMonth: ValueExpression<DayOfMonth>,
    val month: ValueExpression<Month>,
    val dayOfWeek: ValueExpression<DayOfWeek>
)
