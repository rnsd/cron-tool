package nl.rnsd.cron.evaluator.adapter

import nl.rnsd.cron.evaluator.application.port.CronEvaluator
import nl.rnsd.cron.evaluator.application.CronEvaluationResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cron")
@CrossOrigin
class CronExpressionController @Autowired constructor(
    private val cronEvaluator: CronEvaluator
) {

    @GetMapping("/evaluate")
    fun getCronExpression(
        @RequestParam minute: String,
        @RequestParam hour: String,
        @RequestParam dayOfMonth: String,
        @RequestParam month: String,
        @RequestParam dayOfWeek: String
    ): ResponseEntity<CronEvaluationResult> {
            val evaluationResult = cronEvaluator.evaluate(minute, hour, dayOfMonth, month, dayOfWeek)
            return ResponseEntity.ok(evaluationResult)
    }

}
