package me.rasztabiga.thesis.order.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/order")
class OrderController {

    @GetMapping
    fun helloWorld(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World from Order Service!")
    }
}
