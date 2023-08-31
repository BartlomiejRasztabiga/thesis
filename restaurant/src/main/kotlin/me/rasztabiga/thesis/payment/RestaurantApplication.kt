package me.rasztabiga.thesis.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RestaurantApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<RestaurantApplication>(*args)
}
