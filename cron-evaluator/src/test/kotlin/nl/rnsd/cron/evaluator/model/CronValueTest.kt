package nl.rnsd.cron.evaluator.model

import org.assertj.core.api.Assertions
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.ValueSource

class CronValueTest {

    @Test
    fun `creates wildcard instance from string`() {
        val wildcard = CronValue.fromString("*")

        assertThat(wildcard).isEqualTo(WildCard)
    }

    @Test
    fun `creates specific value instance from string`() {
        val specificValue: CronValue = CronValue.fromString("5")

        assertThat(specificValue).isEqualTo(Numeric(5))
    }

    @ParameterizedTest
    @ValueSource(strings = ["a", "$", "II"])
    @EmptySource
    fun `throws illegal arg exception if invalid value is provided`(value: String) {
        Assertions.assertThatThrownBy { CronValue.fromString(value) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid cron value : $value")
    }
}