package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.RangeExpression
import nl.rnsd.cron.evaluator.model.SingularValueExpression
import nl.rnsd.cron.evaluator.model.StepExpression
import nl.rnsd.cron.evaluator.model.ValueExpression

data class Range(val start: Int, val end: Int)

/**
 * This class provides methods to merge overlapping ranges and singular values in a list of value expressions.
 * The resulting list will have merged ranges and singular values, while step expressions are left untouched.
 *
 * @param <T> the type of the CronUnit
 */
class RangeOptimizer {

    /**
     * Given a list of valueExpressions, merge overlapping ranges and singular values (e.g. 1-6, 7, 2-8 -> 1-8)
     * Note : step expressions are excluded from this merging process.
     */
    fun <T : CronUnit> mergeOverlappingRanges(
        valueExpressions: List<ValueExpression<T>>,
        unitSupplier: (String) -> T
    ): List<ValueExpression<T>> {
        val stepExpressions = valueExpressions.filter { it is StepExpression }
        val ranges = valueExpressions
            .asSequence()
            .distinct() //remove duplicates
            .filter { it is RangeExpression || it is SingularValueExpression }
            .map { toRange(it) }
            .sortedBy { it.start }
            .toMutableList()

        val mergedRanges = mergeOverlappingRanges(ranges)
        val convertedRanges = mergedRanges.map { toValueExpression(it, unitSupplier) }.toMutableList()
        convertedRanges.addAll(stepExpressions)

        return convertedRanges
    }

    private fun mergeOverlappingRanges(ranges: MutableList<Range>): List<Range> {
        var i = 0;
        while (i < ranges.size - 1) {
            if (overlappingRanges(ranges[i],ranges[i + 1])) {
                val newRange = merge(ranges[i], ranges[i + 1])
                ranges.removeAt(i)
                ranges[i] = newRange
            } else {
                i++
            }
        }
        return ranges
    }

    private fun <T : CronUnit> toValueExpression(range: Range, unitSupplier: (String) -> T): ValueExpression<T> {
        return if (range.start == range.end) {
            SingularValueExpression(unitSupplier(range.start.toString()))
        } else
            RangeExpression(unitSupplier(range.start.toString()), unitSupplier(range.end.toString()))
    }

    private fun merge(firstRange: Range, secondRange: Range): Range =
        Range(firstRange.start, maxOf(firstRange.end, secondRange.end))

    private fun overlappingRanges(firstRange: Range, secondRange: Range): Boolean =
        firstRange.end >= secondRange.start

    private fun <T : CronUnit> toRange(valueExpression: ValueExpression<T>): Range {
        return when (valueExpression) {
            is RangeExpression -> toRange(valueExpression)
            is SingularValueExpression -> toRange(valueExpression)
            else -> throw IllegalArgumentException("Invalid expression for Pair conversion")
        }
    }

    private fun <T : CronUnit> toRange(rangeExpression: RangeExpression<T>): Range {
        val startOfRange = (rangeExpression.start.value as Numeric).value
        val endOfRange = (rangeExpression.end.value as Numeric).value
        return Range(startOfRange, endOfRange)
    }

    private fun <T : CronUnit> toRange(singularValueExpression: SingularValueExpression<T>): Range {
        val value = (singularValueExpression.cronUnit.value as Numeric).value
        return Range(value, value)
    }

}