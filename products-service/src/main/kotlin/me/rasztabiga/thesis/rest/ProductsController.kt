package me.rasztabiga.thesis.rest

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductsController {

    @PostMapping
    fun createProduct(): String {
        return "HTTP POST handled"
    }

    @GetMapping
    fun getProducts(): String {
        return "HTTP GET handled"
    }

    @PutMapping
    fun updateProduct(): String {
        return "HTTP PUT handled"
    }

    @DeleteMapping
    fun deleteProduct(): String {
        return "HTTP DELETE handled"
    }

}
