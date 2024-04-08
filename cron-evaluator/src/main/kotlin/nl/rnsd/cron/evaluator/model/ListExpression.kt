package nl.rnsd.cron.evaluator.model

data class ListExpression<T : CronUnit>(val valueExpressions: List<ValueExpression<T>>) : ValueExpression<T>() {

    override fun describe(): String {
        val baseExpression = valueExpressions[0].describe()!!
        val remainingExpressions = valueExpressions.drop(1).map { valueExpressionToString(it) }
        return formatExpressions(listOf(baseExpression) + remainingExpressions)
    }

    private fun valueExpressionToString(expression: ValueExpression<T>): String {
        return when (expression) {
            is RangeExpression,
            is StepExpression -> expression.describe()!!.removePrefix("at ")

            is SingularValueExpression -> expression.cronUnit.valueDescription
            else -> error("Type of expression not allowed in listExpression")
        }
    }

    private fun formatExpressions(list: List<String>): String {
        val firstElement = list.first()
        val middleElements = list.drop(1).dropLast(1).joinToString(separator = "") { ", $it" }
        val lastElement = "and ${list.last()}"

        return "$firstElement$middleElements $lastElement"
    }

}