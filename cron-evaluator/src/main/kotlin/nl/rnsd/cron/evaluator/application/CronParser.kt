package nl.rnsd.cron.evaluator.application

import nl.rnsd.cron.evaluator.model.CronExpression

class CronParser {

    fun parse(cronString: String): CronExpression {
        val cronExpresssionElements = cronString
            .trim()
            .replace("\\s+".toRegex(), " ")
            .split(" ")

        require(cronExpresssionElements.size == 5) { "Invalid cron expression string, should consist of 5 elements" }

        return CronExpression(
            ExpressionFactory.create(cronExpresssionElements[0]) { s -> CronUnitFactory.createMinute(s) },
            ExpressionFactory.create(cronExpresssionElements[1]) { s -> CronUnitFactory.createHour(s) },
            ExpressionFactory.create(cronExpresssionElements[2]) { s -> CronUnitFactory.createDayOfMonth(s) },
            ExpressionFactory.create(cronExpresssionElements[3]) { s -> CronUnitFactory.createMonth(s) },
            ExpressionFactory.create(cronExpresssionElements[4]) { s -> CronUnitFactory.createDayOfWeek(s) }
        )
    }

}