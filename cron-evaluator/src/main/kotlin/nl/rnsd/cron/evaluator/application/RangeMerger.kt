package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.expression.RangeExpression
import nl.rnsd.cron.evaluator.model.expression.SingularValueExpression
import nl.rnsd.cron.evaluator.model.expression.StepExpression
import nl.rnsd.cron.evaluator.model.expression.ValueExpression

/**
 * This class provides methods to merge overlapping ranges and singular values in a list of value expressions.
 * The resulting list will have merged ranges and singular values, while step expressions are left untouched.
 *
 * @param <T> the type of the CronUnit
 */
class RangeMerger {

    /**
     * Given a list of valueExpressions, merge overlapping ranges and singular values (e.g. 1-6, 7, 2-8 -> 1-8)
     * Note : step expressions are excluded from this merging process.
     */
    fun <T : CronUnit> mergeOverlappingRanges(
        valueExpressions: List<ValueExpression<T>>,
        unitSupplier: (String) -> T
    ): List<ValueExpression<T>> {

        val ranges = extractRanges(valueExpressions)
        val convertedRanges = mergeOverlappingRanges(ranges)
            .map { toValueExpression(it, unitSupplier) }
            .toMutableList()
        convertedRanges.addAll(valueExpressions.filter { it is StepExpression })

        return convertedRanges
    }

    private fun <T : CronUnit> extractRanges(valueExpressions: List<ValueExpression<T>>) =
        valueExpressions
            .asSequence()
            .distinct() //remove duplicates
            .filter { it is RangeExpression || it is SingularValueExpression }
            .map { toRange(it) }
            .sortedBy { it.start }
            .toMutableList()

    private fun mergeOverlappingRanges(ranges: MutableList<Range>): List<Range> {
        val sortedRanges = ranges.sortedBy { it.start }

        val result = mutableListOf<Range>()
        val rangeIterator = sortedRanges.iterator()
        var currentRange = rangeIterator.next()

        while (rangeIterator.hasNext()) {
            val nextRange = rangeIterator.next()
            if (overlappingRanges(currentRange, nextRange)) {
                currentRange = merge(currentRange, nextRange)
            } else {
                result.add(currentRange)
                currentRange = nextRange
            }
        }
        result.add(currentRange)
        return result.toList()
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
            is RangeExpression -> Range.from(valueExpression)
            is SingularValueExpression -> Range.from(valueExpression)
            else -> throw IllegalArgumentException("Invalid expression for Pair conversion")
        }
    }

    data class Range(val start: Int, val end: Int) {

        companion object {
            fun <T : CronUnit> from(rangeExpression: RangeExpression<T>): Range {
                val startOfRange = rangeExpression.startValue()
                val endOfRange = rangeExpression.endValue()
                return Range(startOfRange, endOfRange)
            }

            fun <T : CronUnit> from(singularValueExpression: SingularValueExpression<T>): Range {
                val value = (singularValueExpression.cronUnit.value as Numeric).value
                return Range(value, value)
            }
        }
    }
}