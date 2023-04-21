package me.rasztabiga.thesis.restaurant.config

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
        return http.authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated()
            .and()
            .csrf().disable()
            .cors().disable()
            .oauth2ResourceServer().jwt().and().and()
            .build()
    }
}

@Configuration
@EnableWebFluxSecurity
@Profile("test")
class TestSecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.authorizeExchange()
            .anyExchange().permitAll()
            .and()
            .csrf().disable()
            .cors().disable()
            .build()
    }
}
