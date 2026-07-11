package com.eventpulse.api

import com.eventpulse.api.config.OpenApiConfig
import io.swagger.v3.oas.models.OpenAPI
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventPulseApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var openApi: OpenAPI

    @Autowired
    private lateinit var openApiConfig: OpenApiConfig

    @Test
    fun contextLoads() {
        assertThat(openApiConfig).isNotNull
        assertThat(openApi.info.title).isEqualTo("EventPulse API")
        assertThat(openApi.info.version).isEqualTo("v0")
    }

    @Test
    fun healthEndpointReturnsUp() {
        mockMvc.get("/api/v1/health")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("UP") }
                jsonPath("$.service") { value("eventpulse-api") }
                jsonPath("$.timestamp") { exists() }
            }
    }

    @Test
    fun openApiDocsAreAvailable() {
        mockMvc.get("/v3/api-docs")
            .andExpect {
                status { isOk() }
                jsonPath("$.openapi") { exists() }
                jsonPath("$.info.title") { value("EventPulse API") }
                jsonPath("$.paths['/api/v1/health']") { exists() }
            }
    }

    @Test
    fun swaggerUiIsAvailable() {
        mockMvc.get("/swagger-ui.html")
            .andExpect {
                status { isFound() }
            }
    }
}
