package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.expression.ListExpression
import nl.rnsd.cron.evaluator.model.expression.RangeExpression
import nl.rnsd.cron.evaluator.model.expression.SingularValueExpression
import nl.rnsd.cron.evaluator.model.expression.StepExpression
import nl.rnsd.cron.evaluator.model.expression.ValueExpression

object ExpressionFactory {

    fun <T : CronUnit> createValueExpression(expression: String, unitSupplier: (String) -> T): Result<ValueExpression<T>> {
        return try {
            Result.success(create(expression) { s -> unitSupplier(s) })
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Unknown error"))
        }
    }

    private fun <T : CronUnit> create(
        expression: String,
        unitSupplier: (String) -> T
    ): ValueExpression<T> {
        return when {
            expression.contains(",") -> listExpression(expression, unitSupplier)
            expression.contains("/") -> stepExpression(expression, unitSupplier)
            expression.contains("-") -> rangeExpression(expression, unitSupplier)
            else -> singularValueExpression(expression, unitSupplier)
        }
    }

    private fun <T : CronUnit> singularValueExpression(
        expression: String,
        unitSupplier: (String) -> T
    ): SingularValueExpression<T> {
        val cronUnit = unitSupplier(expression)
        return SingularValueExpression(cronUnit)
    }

    private fun <T : CronUnit> rangeExpression(
        expression: String,
        unitSupplier: (String) -> T
    ): RangeExpression<T> {
        val range = expression.split("-")
        require(range.size == 2) { "Range should consist of exactly two values" }

        return RangeExpression(unitSupplier(range[0]), unitSupplier(range[1]))
    }

    private fun <T : CronUnit> stepExpression(expression: String, unitSupplier: (String) -> T): StepExpression<T> {
        val expressions: List<String> = expression.split("/")
        require(expressions.size == 2) { "Step should consist of exactly two values" }

        return if (expressions[0] == "*") {
            StepExpression.WildCardStep(unitSupplier(expressions[1]))
        } else {
            val rangeExpression = rangeExpression(expressions[0], unitSupplier)
            return StepExpression.RangeStep(rangeExpression, unitSupplier(expressions[1]))
        }
    }

    private fun <T : CronUnit> listExpression(expression: String, unitSupplier: (String) -> T): ValueExpression<T> {

        require(!expression.contains("*")) { "List expression cannot contain wildcard" }

        val valueExpressions = expression
            .split(",")
            .map { createValueExpression(it, unitSupplier).getOrThrow() }

        val optimizedRanges = RangeMerger().mergeOverlappingRanges(valueExpressions, unitSupplier)
        return when (optimizedRanges.size) {
            1 -> optimizedRanges.first()
            else -> ListExpression(optimizedRanges.toList())
        }
    }
}