package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.adapter.`in`.rest.api.OrderItem
import me.rasztabiga.thesis.query.adapter.`in`.rest.api.RestaurantOrderResponse
import me.rasztabiga.thesis.query.domain.query.entity.RestaurantOrderEntity
import me.rasztabiga.thesis.shared.domain.command.event.RestaurantOrderCreatedEvent

object RestaurantOrderMapper {

    fun mapToEntity(event: RestaurantOrderCreatedEvent): RestaurantOrderEntity {
        return RestaurantOrderEntity(
            id = event.restaurantOrderId,
            orderId = event.orderId,
            restaurantId = event.restaurantId,
            items = event.items.map {
                RestaurantOrderEntity.OrderItem(
                    productId = it.productId
                )
            },
            status = RestaurantOrderEntity.OrderStatus.NEW
        )
    }

    fun mapToResponse(entity: RestaurantOrderEntity): RestaurantOrderResponse {
        return RestaurantOrderResponse(
            restaurantOrderId = entity.id,
            items = entity.items.map {
                OrderItem(
                    productId = it.productId
                )
            },
            status = RestaurantOrderResponse.RestaurantOrderStatus.valueOf(entity.status.name)
        )
    }
}
