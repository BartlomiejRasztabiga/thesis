package me.rasztabiga.thesis.shared.config

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.InetAddress

@Component
class HostnameWebFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val hostname = InetAddress.getLocalHost().hostName
        exchange.response.headers.add("X-Hostname", hostname)
        return chain.filter(exchange)
    }
}
