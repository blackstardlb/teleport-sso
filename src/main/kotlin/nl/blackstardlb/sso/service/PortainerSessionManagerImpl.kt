package nl.blackstardlb.sso.service

import io.netty.handler.logging.LogLevel
import nl.blackstardlb.sso.exceptions.AppAuthenticationFailed
import nl.blackstardlb.sso.exceptions.NoJWTException
import nl.blackstardlb.sso.service.models.SimpleUserData
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

@Service
class PortainerSessionManagerImpl(
    @Value("\${nl.blackstardlb.sso.app.portainer.url}") private val portainerURL: String
) : PortainerSessionManager {
    private val httpClient = HttpClient.create().wiretap(
        "reactor.netty.http.client.HttpClient",
        LogLevel.DEBUG,
        AdvancedByteBufFormat.TEXTUAL
    )

    val webClient = WebClient.builder().baseUrl(portainerURL)
        .clientConnector(ReactorClientHttpConnector(httpClient))
        .build()

    override fun fetchJWT(userData: SimpleUserData): String {
        try {
            val postBody = """
               {
                "username": "${userData.appUsername}",
                "password": "${userData.password}"
               }
            """.trimIndent()


            val spec = webClient.method(HttpMethod.POST).uri("/api/auth").bodyValue(postBody)

            val responseMap = toMap(spec)
            return responseMap.blockOptional().orElse(null)?.get("jwt") ?: throw NoJWTException()
        } catch (throwable: Throwable) {
            throw AppAuthenticationFailed("portainer", throwable)
        }
    }

    override fun isJWTValid(jwt: String): Boolean {
        val spec = webClient.method(HttpMethod.GET).uri("/api/system/info").header("Authorization", "Bearer $jwt")
        return spec.exchangeToMono { Mono.just(it.statusCode() == HttpStatus.OK) }.block() == true
    }

    private fun toMap(spec: WebClient.RequestHeadersSpec<*>): Mono<Map<String, String>> {
        return spec.retrieve().bodyToMono(object : ParameterizedTypeReference<Map<String, String>>() {})
    }
}
