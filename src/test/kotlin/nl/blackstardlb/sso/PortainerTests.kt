package nl.blackstardlb.sso

import io.restassured.RestAssured
import io.restassured.RestAssured.`when`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PortainerTests {
    private val baseUrl = "http://localhost"

    @Value(value = "\${local.server.port}")
    private var port: Int = 0

    @Value(value = "\${nl.blackstardlb.sso.app.teleport.token}")
    private lateinit var teleportToken: String

    @BeforeEach
    fun setup() {
        RestAssured.baseURI = baseUrl
        RestAssured.port = port
    }

    @Test
    fun shouldReturn401IfNotAuthenticated() {
        `when`()
            .get("/portainer")
            .then()
            .statusCode(401)
            .body(equalTo("UNAUTHORIZED"))
    }
}
