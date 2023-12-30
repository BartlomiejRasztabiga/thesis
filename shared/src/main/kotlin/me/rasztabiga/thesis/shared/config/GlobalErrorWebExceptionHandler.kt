package me.rasztabiga.thesis.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.sentry.Sentry
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.queryhandling.QueryExecutionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
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

    private fun createError(ex: Throwable): ApiError {
        val error = when (ex) {
            is CommandExecutionException -> {
                when {
                    ex.localizedMessage.contains("not found") -> {
                        ApiError(ex.message!!, NOT_FOUND)
                    }

                    ex.localizedMessage.contains("OUT_OF_RANGE") -> {
                        ApiError("Aggregate identifier must be unique", BAD_REQUEST)
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

            is IllegalArgumentException -> {
                ApiError(ex.message!!, BAD_REQUEST)
            }

            else -> {
                ApiError(ex.message!!, INTERNAL_SERVER_ERROR)
            }
        }

        Sentry.captureException(ex)

        return error
    }
}

data class ApiError(val message: String, val code: HttpStatus)
