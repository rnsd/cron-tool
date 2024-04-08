package nl.rnsd.cron.evaluator.application

import java.time.DateTimeException
import nl.rnsd.cron.evaluator.model.DayOfMonth
import nl.rnsd.cron.evaluator.model.DayOfWeek
import nl.rnsd.cron.evaluator.model.Hour
import nl.rnsd.cron.evaluator.model.Minute
import nl.rnsd.cron.evaluator.model.Month
import nl.rnsd.cron.evaluator.model.Numeric
import nl.rnsd.cron.evaluator.model.WildCard
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class CronUnitFactoryTest {

    @Test
    fun `creates minute from string value`() {
        assertThat(CronUnitFactory.createMinute("56"))
            .isEqualTo(Minute(Numeric(56)))
    }

    @Test
    fun `creates minute from string wildcard value`() {
        assertThat(CronUnitFactory.createMinute("*"))
            .isEqualTo(Minute(WildCard))
    }

    @Test
    fun `given invalid minute value, throws exception`() {
        assertThatThrownBy { CronUnitFactory.createMinute("60") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Minute should be between 0 and 59")
    }

    @Test
    fun `creates hour from string value`() {
        assertThat(CronUnitFactory.createHour("12"))
            .isEqualTo(Hour(Numeric(12)))
    }

    @Test
    fun `creates hour from string wildcard value`() {
        assertThat(CronUnitFactory.createHour("*"))
            .isEqualTo(Hour(WildCard))
    }

    @Test
    fun `given invalid hour value, throws exception`() {
        assertThatThrownBy { CronUnitFactory.createHour("24") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Hour should be between 0 and 23")
    }

    @Test
    fun `creates month from string value`() {
        assertThat(CronUnitFactory.createMonth("12"))
            .isEqualTo(Month(Numeric(12)))
    }

    @Test
    fun `creates month from string wildcard value`() {
        assertThat(CronUnitFactory.createMonth("*"))
            .isEqualTo(Month(WildCard))
    }

    @Test
    fun `given invalid month value, throws exception`() {
        assertThatThrownBy { CronUnitFactory.createMonth("13") }
            .isInstanceOf(DateTimeException::class.java)
            .hasMessageContaining("Invalid value for MonthOfYear: 13")
    }

    @Test
    fun `creates day of month from string value`() {
        assertThat(CronUnitFactory.createDayOfMonth("12"))
            .isEqualTo(DayOfMonth(Numeric(12)))
    }

    @Test
    fun `creates day of month from string wildcard value`() {
        assertThat(CronUnitFactory.createDayOfMonth("*"))
            .isEqualTo(DayOfMonth(WildCard))
    }

    @Test
    fun `given invalid day of month value, throws exception`() {
        assertThatThrownBy { CronUnitFactory.createDayOfMonth("32") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("DayOfMonth should be between 1 and 31")
    }

    @Test
    fun `creates day of week from string value`() {
        assertThat(CronUnitFactory.createDayOfWeek("1"))
            .isEqualTo(DayOfWeek(Numeric(1)))
    }

    @Test
    fun `creates day of week from string wildcard value`() {
        assertThat(CronUnitFactory.createDayOfWeek("*"))
            .isEqualTo(DayOfWeek(WildCard))
    }

    @Test
    fun `given invalid day of week value, throws exception`() {
        assertThatThrownBy { CronUnitFactory.createDayOfWeek("8") }
            .isInstanceOf(DateTimeException::class.java)
            .hasMessageContaining("Invalid value for DayOfWeek: 8")
    }


}