package me.rasztabiga.thesis.shared.config

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Profile("nosecurity")
class TestUserContextWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val userIdHeader = exchange.request.headers["X-User-Id"]?.firstOrNull()
        val userId = userIdHeader ?: "userId"
        exchange.setUserId(userId)
        return chain.filter(exchange)
    }
}
