package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderCreatedEvent
import java.time.Instant

object RestaurantOrderMapper {

    fun mapToEntity(event: RestaurantOrderCreatedEvent): RestaurantOrderEntity {
        return RestaurantOrderEntity(
            id = event.restaurantOrderId,
            orderId = event.orderId,
            restaurantId = event.restaurantId,
            items = event.items,
            status = RestaurantOrderEntity.OrderStatus.NEW,
            createdAt = Instant.now()
        )
    }

    fun mapToResponse(entity: RestaurantOrderEntity): RestaurantOrderResponse {
        return RestaurantOrderResponse(
            restaurantOrderId = entity.id,
            orderId = entity.orderId,
            items = entity.items,
            status = RestaurantOrderResponse.RestaurantOrderStatus.valueOf(entity.status.name),
            createdAt = entity.createdAt
        )
    }
}
