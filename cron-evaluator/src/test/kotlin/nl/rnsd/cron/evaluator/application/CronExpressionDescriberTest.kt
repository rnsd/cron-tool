package nl.rnsd.cron.evaluator.application

import java.util.stream.Stream
import nl.rnsd.cron.evaluator.model.CronExpression
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CronExpressionDescriberTest {

    @ParameterizedTest
    @MethodSource("singularValueExpressions")
    fun `describes cron expression consisting of singular value expressions`(
        cronExpression: CronExpression,
        expected: String
    ) {
        val result = CronExpressionDescriber().explain(cronExpression)
        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("rangeExpressions")
    fun `describes cron expression consisting of range expressions`(
        cronExpression: CronExpression,
        expected: String
    ) {
        val result = CronExpressionDescriber().explain(cronExpression)
        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("stepExpressions")
    fun `describes cron expression consisting of step expressions`(
        cronExpression: CronExpression,
        expected: String
    ) {
        val result = CronExpressionDescriber().explain(cronExpression)
        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @MethodSource("listExpressions")
    fun `describes cron expression consisting of list expressions`(
        cronExpression: CronExpression,
        expected: String
    ) {
        val result = CronExpressionDescriber().explain(cronExpression)
        assertThat(result).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun singularValueExpressions(): Stream<Arguments> = Stream.of(
            //minute-hour
            Arguments.of(parse("* * * * *"), "At every minute."),
            Arguments.of(parse("2 * * * *"), "At minute 2."),
            Arguments.of(parse("* 1 * * *"), "At every minute past hour 1."),
            Arguments.of(
                parse("2 12 * * *"),
                "At 12:02."
            ), //enige uitzondering ? min singular numeric AND hour numeric singular : maak tijd

            //minute-hour ++
            Arguments.of(parse("* * 1 * *"), "At every minute on day-of-month 1."),
            Arguments.of(parse("2 * 1 * *"), "At minute 2 on day-of-month 1."),
            Arguments.of(parse("2 3 1 * *"), "At 03:02 on day-of-month 1."),
            Arguments.of(parse("* * * 1 *"), "At every minute in January."),
            Arguments.of(parse("2 * * 2 *"), "At minute 2 in February."),
            Arguments.of(parse("* 1 * 3 *"), "At every minute past hour 1 in March."),
            Arguments.of(parse("2 1 * 4 *"), "At 01:02 in April."),
            Arguments.of(parse("* * 1 5 *"), "At every minute on day-of-month 1 in May."),
            Arguments.of(parse("2 * 1 6 *"), "At minute 2 on day-of-month 1 in June."),
            Arguments.of(parse("2 3 1 7 *"), "At 03:02 on day-of-month 1 in July."),
            Arguments.of(parse("* * * * 6"), "At every minute on Saturday."),
            Arguments.of(parse("* * * 8 0"), "At every minute on Sunday in August."),
            Arguments.of(parse("2 * * 9 7"), "At minute 2 on Sunday in September."),
            Arguments.of(parse("* 1 * 10 1"), "At every minute past hour 1 on Monday in October."),
            Arguments.of(parse("2 1 * 11 2"), "At 01:02 on Tuesday in November."),
            Arguments.of(parse("* * 1 12 3"), "At every minute on day-of-month 1 and on Wednesday in December."),
            Arguments.of(parse("2 * 1 1 4"), "At minute 2 on day-of-month 1 and on Thursday in January."),
            Arguments.of(parse("2 3 1 1 5"), "At 03:02 on day-of-month 1 and on Friday in January."),
        )

        @JvmStatic
        fun rangeExpressions(): Stream<Arguments> = Stream.of(
            //minute-hour
            Arguments.of(parse("1-5 * * * *"), "At every minute from 1 through 5."),
            Arguments.of(parse("3-5 1 * * *"), "At every minute from 3 through 5 past hour 1."),
            Arguments.of(parse("1-5 1-5 * * *"), "At every minute from 1 through 5 past every hour from 1 through 5."),
            Arguments.of(parse("* 1-5 * * *"), "At every minute past every hour from 1 through 5."),
            Arguments.of(parse("1 1-5 * * *"), "At minute 1 past every hour from 1 through 5."),
            //minute-hour
            Arguments.of(parse("* * 1-5 * *"), "At every minute on every day-of-month from 1 through 5."),
            Arguments.of(parse("* * * 1-5 *"), "At every minute in every month from January through May."),
            Arguments.of(parse("* * * * 1-5"), "At every minute on every day-of-week from Monday through Friday."),
        )

        @JvmStatic
        fun stepExpressions(): Stream<Arguments> = Stream.of(
            //minute-hour
            Arguments.of(parse("1-10/2 * * * *"), "At every 2nd minute from 1 through 10."),
            Arguments.of(parse("*/2 * * * *"), "At every 2nd minute."),
            Arguments.of(parse("*/3 * * * *"), "At every 3rd minute."),
            Arguments.of(parse("*/4 * * * *"), "At every 4th minute."),
            Arguments.of(parse("*/1 * * * *"), "At every minute."),
            Arguments.of(parse("* */4 * * *"), "At every minute past every 4th hour."),
            Arguments.of(parse("*/2 */4 * * *"), "At every 2nd minute past every 4th hour."),
            Arguments.of(parse("1-10/5 */4 * * *"), "At every 5th minute from 1 through 10 past every 4th hour."),
            Arguments.of(
                parse("1-10/5 12-20/4 * * *"),
                "At every 5th minute from 1 through 10 past every 4th hour from 12 through 20."
            ),
            //minute-hour ++
            Arguments.of(parse("* * */5 * *"), "At every minute on every 5th day-of-month."),
            Arguments.of(parse("* * 1-10/2 * *"), "At every minute on every 2nd day-of-month from 1 through 10."),
            Arguments.of(parse("* * * */5 *"), "At every minute in every 5th month."),
            Arguments.of(parse("* * * 1-10/5 *"), "At every minute in every 5th month from January through October."),
            Arguments.of(parse("* * * * */2"), "At every minute on every 2nd day-of-week."),
            Arguments.of(
                parse("* * * * 1-6/2"),
                "At every minute on every 2nd day-of-week from Monday through Saturday."
            )
        )

        @JvmStatic
        fun listExpressions() = Stream.of(
            //minute-hour
            Arguments.of(parse("1,2 * * * *"), "At minute 1 and 2."),
            Arguments.of(parse("1,2,3,4 * * * *"), "At minute 1, 2, 3 and 4."),
            Arguments.of(parse("1,2,3,4,5 * * * *"), "At minute 1, 2, 3, 4 and 5."),
            Arguments.of(parse("1,2 1,2 * * *"), "At minute 1 and 2 past hour 1 and 2."),
            Arguments.of(parse("1,2 1 * * *"), "At minute 1 and 2 past hour 1."),
            Arguments.of(parse("* 1,2 * * *"), "At every minute past hour 1 and 2."),
            Arguments.of(parse("1 1,2 * * *"), "At minute 1 past hour 1 and 2."),
            Arguments.of(parse("1,2,3,6-10 * * * *"), "At minute 1, 2, 3 and every minute from 6 through 10."),
            Arguments.of(
                parse("1,2,3-5,6-10,12 * * * *"),
                "At minute 1, 2, every minute from 3 through 5, every minute from 6 through 10 and 12."
            ),
            Arguments.of(parse("1,2,6-10/2 * * * *"), "At minute 1, 2 and every 2nd minute from 6 through 10."),
            //minute-hour ++
            Arguments.of(parse("* * 1,2,3 * *"), "At every minute on day-of-month 1, 2 and 3."),
            Arguments.of(parse("* * * 1,3 *"), "At every minute in January and March."),
            Arguments.of(parse("* * * 1,2,3 *"), "At every minute in January, February and March."),
            Arguments.of(parse("* * * * 1,2,3"), "At every minute on Monday, Tuesday and Wednesday.")
        )

        private fun parse(expression: String): CronExpression {
            return CronParser().parse(expression)
        }

    }

}