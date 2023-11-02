package me.rasztabiga.thesis.shared.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class HostnameWebFilter(
    @Value("\${HOSTNAME:unknown}")
    private val hostname: String
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        exchange.response.headers.add("X-Hostname", hostname)
        return chain.filter(exchange)
    }
}
