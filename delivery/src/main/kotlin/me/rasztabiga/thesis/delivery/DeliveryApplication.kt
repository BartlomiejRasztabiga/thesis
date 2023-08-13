package me.rasztabiga.thesis.delivery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeliveryApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<DeliveryApplication>(*args)
}
