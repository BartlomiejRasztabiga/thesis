package me.rasztabiga.thesis.restaurant.config

import org.axonframework.commandhandling.CommandExecutionException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
@Order(-2)
class GlobalErrorWebExceptionHandler : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return when (ex) {
            is CommandExecutionException -> {
                if (ex.localizedMessage.contains("The aggregate was not found in the event store")) {
                    exchange.response.statusCode = HttpStatus.NOT_FOUND
                    exchange.response.setComplete()
                } else {
                    exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                    exchange.response.setComplete()
                }
            }

            else -> {
                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                exchange.response.setComplete()
            }
        }
    }
}
