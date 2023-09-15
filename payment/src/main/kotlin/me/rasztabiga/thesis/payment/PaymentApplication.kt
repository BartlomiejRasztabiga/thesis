package me.rasztabiga.thesis.payment

import com.stripe.Stripe
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    // TODO move to secrets
    Stripe.apiKey =
        "sk_test_51NkBWgEKNDTQpRbz21IxN1lSre63zv95KLm787PdcaZRMWv0YhjTqiwVHqlB9CLGtV8BCv4VeaFUdG4SAAXLrBGH00kaWNjf8C"

    runApplication<PaymentApplication>(*args)
}
