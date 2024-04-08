package nl.rnsd.cron.evaluator.model

sealed class CronValue {
    companion object {
        fun fromString(value: String): CronValue {
            return when {
                value == "*" -> WildCard
                value.toIntOrNull() != null -> Numeric(value.toInt())
                else -> throw IllegalArgumentException("Invalid cron value : $value")
            }
        }
    }
    abstract override fun toString(): String
}

object WildCard : CronValue() {
    override fun toString(): String = "*"
}

data class Numeric(val value: Int) : CronValue() {
    override fun toString(): String = value.toString()
}
