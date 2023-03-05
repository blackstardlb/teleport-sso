package nl.blackstardlb.sso.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@PropertySource("file:\${CONFIG_DIR}/devconfig.properties", ignoreResourceNotFound = true)
@PropertySource("file:/config/config.properties", ignoreResourceNotFound = true)
class SecurityConfig {
    @Value("\${nl.blackstardlb.sso.jwt.header}")
    private lateinit var header: String

    @Value("\${nl.blackstardlb.sso.jwt.has_prefix}")
    private var hasPrefix: Boolean = false

    @Bean
    fun filterChain(http: HttpSecurity, bearerTokenResolver: BearerTokenResolver): SecurityFilterChain {
        http
            .oauth2ResourceServer { it.bearerTokenResolver(bearerTokenResolver).jwt(Customizer.withDefaults()) }
            .anonymous()
        return http.build()
    }

    @Bean
    fun customBearerTokenResolver(): BearerTokenResolver {
        return if (hasPrefix) {
            DefaultBearerTokenResolver().also { it.setBearerTokenHeaderName(header) }
        } else {
            HeaderBearerTokenResolver(header)
        }
    }
}
