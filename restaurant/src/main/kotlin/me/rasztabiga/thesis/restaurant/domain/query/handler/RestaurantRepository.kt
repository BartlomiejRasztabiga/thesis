package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import reactor.core.publisher.Flux

interface RestaurantRepository {

    fun add(restaurant: RestaurantEntity)

    fun loadAll(): Flux<RestaurantEntity>
}
