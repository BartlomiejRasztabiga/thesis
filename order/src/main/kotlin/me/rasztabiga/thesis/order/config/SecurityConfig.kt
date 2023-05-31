package me.rasztabiga.thesis.order.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Profile("!test")
class SecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.authorizeExchange {
            it.pathMatchers("/actuator/**").permitAll()
            it.anyExchange().authenticated()
        }.csrf {
            it.disable()
        }.cors {
            it.disable()
        }.oauth2ResourceServer {
            it.jwt {}
        }.build()
    }
}

@Configuration
@EnableWebFluxSecurity
@Profile("test")
class TestSecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.authorizeExchange {
            it.anyExchange().permitAll()
        }.csrf {
            it.disable()
        }.cors {
            it.disable()
        }.build()
    }
}
