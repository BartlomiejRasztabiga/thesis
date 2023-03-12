package me.rasztabiga.thesis.restaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RestaurantApplication

fun main(args: Array<String>) {
	runApplication<RestaurantApplication>(*args)
}
