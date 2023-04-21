package me.rasztabiga.thesis.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GatewayApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}
