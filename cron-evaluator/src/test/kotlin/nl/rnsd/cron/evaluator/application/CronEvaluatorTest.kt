package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronAttribute
import nl.rnsd.cron.evaluator.model.CronEvaluationResult
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test

class CronEvaluatorTest {

    private val cronEvaluator = CronEvaluator(CronExpressionDescriber())

    @Test
    fun `evaluates valid expression`() {
        val result = cronEvaluator.evaluate("*", "*", "*", "*", "*")

        //assert : returns valid expressions with error messages null and adds explanation
        assertThat(result).isNotNull
        assertThat(result!!)
            .extracting(
                CronEvaluationResult::minute,
                CronEvaluationResult::hour,
                CronEvaluationResult::month,
                CronEvaluationResult::dayOfMonth,
                CronEvaluationResult::dayOfWeek,
                CronEvaluationResult::scheduleExplanation)
            .containsExactly(
                CronAttribute("*", null),
                CronAttribute("*", null),
                CronAttribute("*", null),
                CronAttribute("*", null),
                CronAttribute("*", null),
                "At every minute."
            )
    }

    @Test
    fun `evaluates invalid expression`() {
        val result = cronEvaluator.evaluate("65", "25", "40", "*", "*")

        //assert : returns invalid expressions and error messages for invalid expressions, explanation is null
        assertThat(result).isNotNull
        assertThat(result!!)
            .extracting(
                CronEvaluationResult::minute,
                CronEvaluationResult::hour,
                CronEvaluationResult::month,
                CronEvaluationResult::dayOfMonth,
                CronEvaluationResult::dayOfWeek,
                CronEvaluationResult::scheduleExplanation)
            .containsExactly(
                CronAttribute("65", "Minute should be between 0 and 59"),
                CronAttribute("25", "Hour should be between 0 and 23"),
                CronAttribute("*", null),
                CronAttribute("40","DayOfMonth should be between 1 and 31"),
                CronAttribute("*", null),
                null
            )
    }
}