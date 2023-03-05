package nl.blackstardlb.sso.controller

import com.google.common.collect.EvictingQueue
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import mu.KLoggable
import mu.KLogger
import nl.blackstardlb.sso.service.PortainerSessionManager
import nl.blackstardlb.sso.service.UserDataService
import nl.blackstardlb.sso.service.models.SimpleUserData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import java.nio.charset.Charset
import java.util.*

@Controller
@RequestMapping("/portainer")
class PortainerController(
    @Qualifier("portainer") private val userDataService: UserDataService<SimpleUserData>,
    @Value("classpath:portainer.html") private val portainerPage: Resource,
    private val portainerSessionManager: PortainerSessionManager,
) : KLoggable {
    override val logger: KLogger by lazy { logger() }
    private val getJWTHashes = EvictingQueue.create<UUID>(20)

    private fun addHash(response: HttpServletResponse) {
        val hash = UUID.randomUUID()
        getJWTHashes.add(hash)
        response.addCookie(
            Cookie(
                "portainer_auth_step_hash",
                "$hash"
            )
        )
    }

    /**
     * If portainer_auth_step_hash not in the list of know hashes
     *  -> Set portainer_auth_step_hash so we know we have the latest portainer_jwt value
     * If portainer_auth_step_hash in the list of know hashes and portainer_jwt not valid
     *  -> Set new_portainer_jwt so the js page can add it to local storage
     *  -> Set portainer_auth_step_hash so we know we have the latest portainer_jwt value
     * If portainer_auth_step_hash in the list of know hashes and portainer_jwt valid
     *  -> Continue
     */
    @GetMapping
    fun authenticate(
        @RequestHeader headers: Map<String, String>,
        @AuthenticationPrincipal principal: Jwt?,
        @CookieValue("portainer_jwt", required = false) portainerJwt: String?,
        @CookieValue("portainer_auth_step_hash", required = false) portainerAuthStep: String?,
        response: HttpServletResponse
    ): ResponseEntity<Any> {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED")
        if (headers["x-forwarded-uri"] != "/") return ResponseEntity.ok().build()
        if (portainerAuthStep == null || !getJWTHashes.remove(UUID.fromString(portainerAuthStep))) {
            logger.info { "Fetching JWT" }
            addHash(response)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(portainerPage.getContentAsString(Charset.defaultCharset()))
        }
        if (portainerJwt == null || portainerJwt == "null" || !portainerSessionManager.isJWTValid(portainerJwt)) {
            logger.info { "Portainer JWT is not valid do auth" }
            val userData = userDataService.getUserDataByJwtUsername(principal.subject)
            val jwt = portainerSessionManager.fetchJWT(userData)
            addHash(response)
            response.addCookie(
                Cookie(
                    "new_portainer_jwt",
                    "$jwt"
                )
            )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(portainerPage.getContentAsString(Charset.defaultCharset()))
        }
        logger.info { "JWT is valid" }
        return ResponseEntity.ok().build()
    }
}
