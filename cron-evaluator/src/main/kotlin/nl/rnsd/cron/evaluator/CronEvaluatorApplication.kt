package nl.rnsd.cron.evaluator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CronEvaluatorApplication

fun main(args: Array<String>) {
    runApplication<CronEvaluatorApplication>(*args)
}
