package me.rasztabiga.thesis.restaurant

import me.rasztabiga.thesis.shared.config.GlobalErrorWebExceptionHandler
import me.rasztabiga.thesis.shared.security.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(SecurityConfig::class, GlobalErrorWebExceptionHandler::class)
class RestaurantApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<RestaurantApplication>(*args)
}
