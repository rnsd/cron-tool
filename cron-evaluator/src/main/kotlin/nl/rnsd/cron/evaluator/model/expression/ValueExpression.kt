package nl.rnsd.cron.evaluator.model.expression

import nl.rnsd.cron.evaluator.model.CronUnit

sealed class ValueExpression<T : CronUnit> {
    abstract fun describe(): String?
}