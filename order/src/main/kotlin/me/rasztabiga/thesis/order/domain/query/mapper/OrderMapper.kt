package me.rasztabiga.thesis.order.domain.query.mapper

import me.rasztabiga.thesis.order.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity

object OrderMapper {

    fun mapToEntity(event: OrderStartedEvent): OrderEntity {
        return OrderEntity(
            id = event.orderId,
            restaurantId = event.restaurantId,
            userId = event.userId,
            status = OrderEntity.OrderStatus.valueOf(event.status.name)
        )
    }

    fun mapToResponse(entity: OrderEntity): OrderResponse {
        return OrderResponse(
            id = entity.id,
            restaurantId = entity.restaurantId,
            userId = entity.userId,
            status = OrderResponse.OrderStatus.valueOf(entity.status.name)
        )
    }
}
