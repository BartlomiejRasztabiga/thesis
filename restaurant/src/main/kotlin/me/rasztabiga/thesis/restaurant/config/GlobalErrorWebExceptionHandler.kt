package me.rasztabiga.thesis.restaurant.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.queryhandling.QueryExecutionException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
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

    // TODO Can I make it better? Currently Axon swallows all of my original exceptions

    private fun createError(ex: Throwable): ApiError {
        return when (ex) {
            is CommandExecutionException -> {
                when {
                    ex.localizedMessage.contains("not found") -> {
                        ApiError(ex.message!!, NOT_FOUND)
                    }

                    else -> {
                        ApiError(ex.message!!, INTERNAL_SERVER_ERROR)
                    }
                }
            }

            is QueryExecutionException -> {
                when {
                    ex.localizedMessage.contains("not found") -> {
                        ApiError(ex.message!!, NOT_FOUND)
                    }

                    else -> {
                        ApiError(ex.message!!, INTERNAL_SERVER_ERROR)
                    }
                }
            }

            else -> {
                ApiError(ex.message!!, INTERNAL_SERVER_ERROR)
            }
        }
    }
}

data class ApiError(val message: String, val code: HttpStatus)
