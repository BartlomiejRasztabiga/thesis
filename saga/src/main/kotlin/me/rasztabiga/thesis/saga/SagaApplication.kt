package me.rasztabiga.thesis.saga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SagaApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SagaApplication>(*args)
}
