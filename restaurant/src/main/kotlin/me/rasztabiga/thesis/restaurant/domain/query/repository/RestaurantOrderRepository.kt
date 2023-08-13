package me.rasztabiga.thesis.restaurant.domain.query.repository

import me.rasztabiga.thesis.restaurant.domain.query.entity.RestaurantOrderEntity
import reactor.core.publisher.Flux
import java.util.*

interface RestaurantOrderRepository {

    fun save(restaurant: RestaurantOrderEntity)

    fun loadAllByRestaurantId(restaurantId: UUID): Flux<RestaurantOrderEntity>

    fun load(id: UUID): RestaurantOrderEntity?
}
