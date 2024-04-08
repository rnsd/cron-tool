package nl.rnsd.cron.evaluator.model

import nl.rnsd.cron.evaluator.model.util.ExpressionUtils.getMonthName
import nl.rnsd.cron.evaluator.model.util.ExpressionUtils.getWeekdayName

sealed class CronUnit(open val value: CronValue) {

    fun checkIntValueBoundary() {
        (value as? Numeric)?.let {
            val value = it.value
            require(value in minValue..maxValue) {
                "${this.javaClass.simpleName} should be between $minValue and $maxValue"
            }
        }
    }

    abstract val minValue: Int
    abstract val maxValue: Int
    abstract val unitDescription: String
    abstract val valueDescription: String
}

data class Minute(override val value: CronValue) : CronUnit(value) {
    override val minValue = 0
    override val maxValue = 59
    override val unitDescription = "minute"
    override val valueDescription = value.toString()

    constructor(value: Int) : this(Numeric(value))
}

data class Hour(override val value: CronValue) : CronUnit(value) {
    override val minValue = 0
    override val maxValue = 23
    override val unitDescription = "hour"
    override val valueDescription = value.toString()

    constructor(value: Int) : this(Numeric(value))
}

data class Month(override val value: CronValue) : CronUnit(value) {
    override val minValue = 1
    override val maxValue = 12
    override val unitDescription = "month"
    override val valueDescription =
        if (value is WildCard) value.toString() else getMonthName((value as Numeric).value)

    constructor(value: Int) : this(Numeric(value))
}

data class DayOfMonth(override val value: CronValue) : CronUnit(value) {
    override val minValue = 1
    override val maxValue = 31
    override val unitDescription = "day-of-month"
    override val valueDescription = value.toString()

    constructor(value: Int) : this(Numeric(value))
}

data class DayOfWeek(override val value: CronValue) : CronUnit(value) {
    override val minValue = 1
    override val maxValue = 7
    override val unitDescription = "day-of-week"
    override val valueDescription =
        if (value is WildCard) value.toString() else getWeekdayName((value as Numeric).value)

    constructor(value: Int) : this(Numeric(if (value == 0) 7 else value))
}
