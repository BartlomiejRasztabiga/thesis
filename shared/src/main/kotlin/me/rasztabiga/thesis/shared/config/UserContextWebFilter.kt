package me.rasztabiga.thesis.shared.config

import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Profile("!test")
class UserContextWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
            .mapNotNull { it.authentication.principal }
            .cast(Jwt::class.java)
            .doOnNext {
                val userId = it.subject
                exchange.setUserId(userId)
            }
            .then(chain.filter(exchange))
    }
}
