package me.rasztabiga.thesis.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OrderApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<OrderApplication>(*args)
}
