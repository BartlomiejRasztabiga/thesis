package me.rasztabiga.thesis.restaurant.domain.query.handler

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantEntity
import reactor.core.publisher.Flux
import java.util.UUID

interface RestaurantRepository {

    fun save(restaurant: RestaurantEntity)

    fun load(id: UUID): RestaurantEntity?

    fun loadAll(): Flux<RestaurantEntity>

    fun delete(id: UUID)
}
