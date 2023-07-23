package me.rasztabiga.thesis.shared.config

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
@Profile("test")
class TestUserContextWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.setUserId("userId")
        return chain.filter(exchange)
    }
}
