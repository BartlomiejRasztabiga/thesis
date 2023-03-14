package me.rasztabiga.thesis.restaurant.adapter.`in`.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/restaurant")
class RestaurantController {

    @GetMapping
    fun helloWorld(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello World from Restaurant Service!")
    }
}
