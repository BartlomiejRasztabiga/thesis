package me.rasztabiga.thesis.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<PaymentApplication>(*args)
}
