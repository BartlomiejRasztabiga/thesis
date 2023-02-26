package me.rasztabiga.thesis.products

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductsApplication

fun main(args: Array<String>) {
	runApplication<ProductsApplication>(*args)
}
