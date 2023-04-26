package me.rasztabiga.thesis.restaurant.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.axonframework.commandhandling.CommandExecutionException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
@Order(-2)
class GlobalErrorWebExceptionHandler(
    private val objectMapper: ObjectMapper
) : ErrorWebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val bufferFactory = exchange.response.bufferFactory()
        val error = createError(ex)
        val dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(error))

        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        exchange.response.statusCode = error.code
        return exchange.response.writeWith(Mono.just(dataBuffer))
    }

    private fun createError(ex: Throwable): ApiError {
        return when (ex) {
            is CommandExecutionException -> {
                when {
                    ex.localizedMessage.contains("The aggregate was not found in the event store") -> {
                        ApiError("The requested resource was not found", NOT_FOUND)
                    }

                    else -> {
                        ApiError("An unexpected error occurred", INTERNAL_SERVER_ERROR)
                    }
                }
            }

            else -> {
                ApiError("An unexpected error occurred", INTERNAL_SERVER_ERROR)
            }
        }
    }
}

data class ApiError(val message: String, val code: HttpStatus)
