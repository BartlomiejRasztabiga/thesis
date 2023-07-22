package me.rasztabiga.thesis.order

import me.rasztabiga.thesis.shared.config.GlobalErrorWebExceptionHandler
import me.rasztabiga.thesis.shared.security.SecurityConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(SecurityConfig::class, GlobalErrorWebExceptionHandler::class)
class OrderApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<OrderApplication>(*args)
}
