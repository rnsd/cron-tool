package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronValue
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.expression.RangeExpression
import nl.rnsd.cron.evaluator.model.expression.SingularValueExpression
import nl.rnsd.cron.evaluator.model.expression.StepExpression
import nl.rnsd.cron.evaluator.model.expression.ValueExpression
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RangeMergerTest {

    @Test
    fun `given list of value expressions, with overlapping ranges, merges ranges`() {
        val expressions = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(8))
        )

        val optimizedExpressions = RangeMerger()
            .mergeOverlappingRanges(expressions) { str -> Minute(CronValue.fromString(str)) }

        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(8))
        )
    }

    @Test
    fun `given list of multiple value expressions, with multiple overlapping ranges, merges ranges`() {
        val expressions = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(8)),
            RangeExpression(Minute(10), Minute(15)),
            RangeExpression(Minute(14), Minute(20)),
        )

        val optimizedExpressions = RangeMerger()
            .mergeOverlappingRanges(expressions) { str -> Minute(CronValue.fromString(str)) }


        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(8)),
            RangeExpression(Minute(10), Minute(20)),
        )
    }

    @Test
    fun `given list of value expressions, with non-overlapping ranges, does not merge ranges`() {
        val expressions: List<RangeExpression<Minute>> = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(7), Minute(10))
        )

        val optimizedExpressions: List<ValueExpression<Minute>> =
            RangeMerger().mergeOverlappingRanges(expressions, { str: String -> Minute(CronValue.fromString(str)) })

        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(7), Minute(10))
        )
    }

    @Test
    fun `given list of multiple value expressions, with multiple overlapping and non-overlapping ranges, partially merges ranges`() {
        val expressions = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(8)),
            RangeExpression(Minute(12), Minute(15)),
            RangeExpression(Minute(16), Minute(18)),
            RangeExpression(Minute(17), Minute(20)),
            RangeExpression(Minute(25), Minute(28))
        )

        val optimizedExpressions = RangeMerger()
            .mergeOverlappingRanges(expressions) { str -> Minute(CronValue.fromString(str)) }


        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(8)),
            RangeExpression(Minute(12), Minute(15)),
            RangeExpression(Minute(16), Minute(20)),
            RangeExpression(Minute(25), Minute(28)),
        )
    }

    @Test
    fun `given list of multiple value expressions, with multiple overlapping ranges and singular values, merges ranges and values`() {
        val expressions = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(8)),
            SingularValueExpression(Minute(7)),
            RangeExpression(Minute(16), Minute(18)),
            RangeExpression(Minute(17), Minute(20)),
            SingularValueExpression(Minute(19))
        )

        val optimizedExpressions = RangeMerger()
            .mergeOverlappingRanges(expressions) { str -> Minute(CronValue.fromString(str)) }


        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(8)),
            RangeExpression(Minute(16), Minute(20))
        )
    }

    @Test
    fun `given list of multiple value expressions, with multiple overlapping and non-overlapping ranges and singular values, partially merges ranges and values`() {
        val expressions = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(8)),
            SingularValueExpression(Minute(7)),
            SingularValueExpression(Minute(10)),
            RangeExpression(Minute(16), Minute(18)),
            RangeExpression(Minute(17), Minute(20)),
            SingularValueExpression(Minute(21)),
            SingularValueExpression(Minute(19))
        )

        val optimizedExpressions = RangeMerger()
            .mergeOverlappingRanges(expressions) { str -> Minute(CronValue.fromString(str)) }


        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(8)),
            SingularValueExpression(Minute(10)),
            RangeExpression(Minute(16), Minute(20)),
            SingularValueExpression(Minute(21))
        )
    }


    @Test
    fun `given list of value expressions, with overlapping range expressions and step expressions, merge ranges and leaves step expressions untouched`() {
        val expressions: List<ValueExpression<Minute>> = listOf(
            RangeExpression(Minute(1), Minute(5)),
            RangeExpression(Minute(4), Minute(10)),
            StepExpression.RangeStep(
                RangeExpression(
                    Minute(5),
                    Minute(20)
                ), Minute(5)
            )
        )

        val optimizedExpressions: List<ValueExpression<Minute>> =
            RangeMerger().mergeOverlappingRanges(expressions) { str: String -> Minute(CronValue.fromString(str)) }

        assertThat(optimizedExpressions).containsExactly(
            RangeExpression(Minute(1), Minute(10)),
            StepExpression.RangeStep(
                RangeExpression(
                    Minute(5),
                    Minute(20)
                ), Minute(5)
            )
        )
    }
}