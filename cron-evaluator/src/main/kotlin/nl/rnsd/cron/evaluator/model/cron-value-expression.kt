package nl.rnsd.cron.evaluator.model

data class CronExpression(
    val minute: ValueExpression<Minute>,
    val hour: ValueExpression<Hour>,
    val dayOfMonth: ValueExpression<DayOfMonth>,
    val month: ValueExpression<Month>,
    val dayOfWeek: ValueExpression<DayOfWeek>
)

sealed class ValueExpression<T : CronUnit> {
    abstract fun describe(): String?
}