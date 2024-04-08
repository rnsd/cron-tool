package nl.rnsd.cron.evaluator.model

data class CronAttribute(val expression: String, val errorMessage: String?)

data class CronEvaluationResult(
    val minute: CronAttribute,
    val hour: CronAttribute,
    val dayOfMonth: CronAttribute,
    val month: CronAttribute,
    val dayOfWeek: CronAttribute,
    val scheduleExplanation: String?
) {
    class Builder {
        private lateinit var minute: CronAttribute
        private lateinit var hour: CronAttribute
        private lateinit var dayOfMonth: CronAttribute
        private lateinit var month: CronAttribute
        private lateinit var dayOfWeek: CronAttribute
        private var scheduleExplanation: String? = null

        fun minute(minute: CronAttribute) = apply { this.minute = minute }
        fun hour(hour: CronAttribute) = apply { this.hour = hour }
        fun dayOfMonth(dayOfMonth: CronAttribute) = apply { this.dayOfMonth = dayOfMonth }
        fun month(month: CronAttribute) = apply { this.month = month }
        fun dayOfWeek(dayOfWeek: CronAttribute) = apply { this.dayOfWeek = dayOfWeek }
        fun scheduleExplanation(scheduleExplanation: String?) = apply { this.scheduleExplanation = scheduleExplanation }

        fun build() = CronEvaluationResult(
            minute,
            hour,
            dayOfMonth,
            month,
            dayOfWeek,
            scheduleExplanation
        )
    }
}