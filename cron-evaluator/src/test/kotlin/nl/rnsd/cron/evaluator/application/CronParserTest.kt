package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronUnit
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.ListExpression
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.RangeExpression
import nl.rnsd.cron.evaluator.model.SingularValueExpression
import nl.rnsd.cron.evaluator.model.StepExpression
import nl.rnsd.cron.evaluator.model.ValueExpression
import nl.rnsd.cron.evaluator.model.WildCard
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CronParserTest {

    @Test
    fun `parses expression containing only wildcards`() {
        val cronExpression = CronParser().parse("* * * * *")

        assertSingularValue(cronExpression.minute, Minute(WildCard))
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `parses expression containing only singular values`() {
        val cronExpression = CronParser().parse("1 2 3 4 5")

        assertSingularValue(cronExpression.minute, Minute(Numeric(1)))
        assertSingularValue(cronExpression.hour, Hour(Numeric(2)))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(Numeric(3)))
        assertSingularValue(cronExpression.month, Month(Numeric(4)))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(Numeric(5)))
    }

    @Test
    fun `parses range expressions`() {
        val cronExpression = CronParser().parse("0-59 0-23 1-31 1-12 1-7")

        assertRange(cronExpression.minute, Minute(Numeric(0)), Minute(Numeric(59)))
        assertRange(cronExpression.hour, Hour(Numeric(0)), Hour(Numeric(23)))
        assertRange(cronExpression.dayOfMonth, DayOfMonth(Numeric(1)), DayOfMonth(Numeric(31)))
        assertRange(cronExpression.month, Month(Numeric(1)), Month(Numeric(12)))
        assertRange(cronExpression.dayOfWeek, DayOfWeek(Numeric(1)), DayOfWeek(Numeric(7)))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "2-1", //start > end
            "1-1", //start == end
            "*-1", //wildcard not allowed
            "1-91" //too large value for minute
        ]
    )
    fun `given range expression, if range is invalid, throw exception`(range: String) {
        val expression = "$range * * * *"
        assertThatThrownBy { CronParser().parse(expression) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `parses step expressions with wildcard`() {
        val cronExpression = CronParser().parse("*/1 */2 */3 */4 */5")

        assertThat(cronExpression.minute).isEqualTo(StepExpression.WildCardStep(Minute(Numeric(1))))
        assertThat(cronExpression.hour).isEqualTo(StepExpression.WildCardStep(Hour(Numeric(2))))
        assertThat(cronExpression.dayOfMonth).isEqualTo(StepExpression.WildCardStep(DayOfMonth(Numeric(3))))
        assertThat(cronExpression.month).isEqualTo(StepExpression.WildCardStep(Month(Numeric(4))))
        assertThat(cronExpression.dayOfWeek).isEqualTo(StepExpression.WildCardStep(DayOfWeek(Numeric(5))))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "*/* * * * *",
            "* */* * * *",
            "* * */* * *",
            "* * * */* *",
            "* * * * */*"
        ]
    )
    fun `given step expression, if step is wildcard, throws exception`(expression: String) {
        assertThatThrownBy { CronParser().parse(expression) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Invalid step expression: step value cannot be wildcard")
    }


    @ParameterizedTest
    @ValueSource(
        strings = [
            "*/0 * * * *",
            "* */0 * * *",
            "* * */0 * *"
        ]
    )
    fun `given step expression, if step value is zero, throws exception`(expression: String) {
        assertThatThrownBy { CronParser().parse(expression) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `parses step expressions with range expression`() {
        val cronExpression = CronParser().parse("10-20/1 12-14/2 21-25/3 4-8/4 1-7/3")

        assertThat(cronExpression.minute).isEqualTo(
            StepExpression.RangeStep(
                RangeExpression(Minute(10), Minute(20)),
                Minute(1)
            )
        )
        assertThat(cronExpression.hour).isEqualTo(
            StepExpression.RangeStep(
                RangeExpression(Hour(12), Hour(14)),
                Hour(Numeric(2))
            )
        )
        assertThat(cronExpression.dayOfMonth).isEqualTo(
            StepExpression.RangeStep(
                RangeExpression(DayOfMonth(21), DayOfMonth(25)),
                DayOfMonth(3)
            )
        )
        assertThat(cronExpression.month).isEqualTo(
            StepExpression.RangeStep(
                RangeExpression(Month(4), Month(8)),
                Month(4)
            )
        )
        assertThat(cronExpression.dayOfWeek).isEqualTo(
            StepExpression.RangeStep(
                RangeExpression(DayOfWeek(1), DayOfWeek(7)),
                DayOfWeek(Numeric(3))
            )
        )
    }

    @Test
    fun `parses list expression with singular expressions`() {
        val cronExpression = CronParser().parse("1,2,3 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            ListExpression(
                listOf(
                    SingularValueExpression(Minute(1)),
                    SingularValueExpression(Minute(2)),
                    SingularValueExpression(Minute(3))
                )
            )
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `parses list expression with range expressions`() {
        val cronExpression = CronParser().parse("1-5,6-10,11-15 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            ListExpression(
                listOf(
                    RangeExpression(Minute(1), Minute(5)),
                    RangeExpression(Minute(6), Minute(10)),
                    RangeExpression(Minute(11), Minute(15))
                )
            )
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `given list expression with duplicate ranges, ranges are merged`() {
        val cronExpression = CronParser().parse("1-5,1-5 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            RangeExpression(Minute(1), Minute(5))
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `given list expression with overlapping ranges, ranges are merged`() {
        val cronExpression = CronParser().parse("1-5,2-8 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            RangeExpression(Minute(1), Minute(8))
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `given list expression with overlapping range with singular value, ranges are merged`() {
        val cronExpression = CronParser().parse("1-5,2 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            RangeExpression(Minute(1), Minute(5))
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `given list expression with overlapping range with multiple singular values, ranges are merged`() {
        val cronExpression = CronParser().parse("1-5,2,3 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            RangeExpression(Minute(1), Minute(5))
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @Test
    fun `given list expression with overlapping range and multiple singular values and step expressions, ranges are merged`() {
        val cronExpression = CronParser().parse("1-5,2,3,4-11,2-8/4 * * * *")

        assertThat(cronExpression.minute).isEqualTo(
            ListExpression(
                listOf(
                    RangeExpression(Minute(1), Minute(11)),
                    StepExpression.RangeStep(
                        RangeExpression(Minute(2), Minute(8)),
                        Minute(4)
                    )
                )
            )
        )
        assertSingularValue(cronExpression.hour, Hour(WildCard))
        assertSingularValue(cronExpression.dayOfMonth, DayOfMonth(WildCard))
        assertSingularValue(cronExpression.month, Month(WildCard))
        assertSingularValue(cronExpression.dayOfWeek, DayOfWeek(WildCard))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "*,1,2 * * * *",
            "1,*,3 * * * *",
            "1,2,* * * * *",
            "1,*-5 * * * *",
            "1,2-* * * * *",
            "1,*/1 * * * *",
        ]
    )
    fun `given list expression with wildcard, throws exception`(cronExpression: String) {
        assertThatThrownBy { CronParser().parse(cronExpression) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("List expression cannot contain wildcard")
    }

    companion object {
        private fun assertSingularValue(expression: ValueExpression<*>, expectedValue: CronUnit) {
            assertThat(expression)
                .satisfies({
                    val singularValue = it as SingularValueExpression<*>
                    assertThat(singularValue.cronUnit).isEqualTo(expectedValue)
                })
        }

        private fun assertRange(
            expression: ValueExpression<*>,
            expectedStartOfRange: CronUnit,
            expectedEndOfRange: CronUnit
        ) {
            assertThat(expression)
                .satisfies({
                    val rangeExpression = it as RangeExpression<*>
                    assertThat(rangeExpression.start).isEqualTo(expectedStartOfRange)
                    assertThat(rangeExpression.end).isEqualTo(expectedEndOfRange)
                })
        }
    }

}