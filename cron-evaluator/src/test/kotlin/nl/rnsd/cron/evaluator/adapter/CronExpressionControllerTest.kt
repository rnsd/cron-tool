package nl.rnsd.cron.evaluator.adapter

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class CronExpressionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `getCronExpression test`() {
        val expectedResponse = """
           {
                "minute": {
                  "expression": "*",
                  "errorMessage": null
                },
                "hour": {
                  "expression": "*",
                  "errorMessage": null
                },
                "dayOfMonth": {
                  "expression": "*",
                  "errorMessage": null
                },
                "month": {
                  "expression": "*",
                  "errorMessage": null
                },
                "dayOfWeek": {
                  "expression": "*",
                  "errorMessage": null
                },
                "scheduleExplanation": "At every minute."
            }
        """.trimIndent()

        //act & assert
        mockMvc.perform(
            get("/cron/evaluate")
                .param("minute", "*")
                .param("hour", "*")
                .param("dayOfMonth", "*")
                .param("month", "*")
                .param("dayOfWeek", "*")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(expectedResponse))
            .andReturn()
    }
}