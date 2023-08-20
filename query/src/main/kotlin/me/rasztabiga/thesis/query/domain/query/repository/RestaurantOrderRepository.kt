package me.rasztabiga.thesis.query.domain.query.repository

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import reactor.core.publisher.Flux
import java.util.*

interface RestaurantOrderRepository {

    fun save(restaurant: RestaurantOrderEntity)

    fun loadAllByRestaurantId(restaurantId: UUID): Flux<RestaurantOrderEntity>

    fun load(id: UUID): RestaurantOrderEntity?

    fun loadByOrderId(orderId: UUID): RestaurantOrderEntity?
}
