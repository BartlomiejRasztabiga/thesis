package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent

object OrderMapper {

    fun mapToEntity(event: OrderStartedEvent): OrderEntity {
        return OrderEntity(
            id = event.orderId,
            restaurantId = event.restaurantId,
            userId = event.userId,
            status = OrderEntity.OrderStatus.valueOf(event.status.name),
            items = mutableListOf(),
            total = null,
            paymentId = null,
            deliveryAddressId = null,
            courierId = null
        )
    }

    fun mapToResponse(entity: OrderEntity): OrderResponse {
        return OrderResponse(
            id = entity.id,
            restaurantId = entity.restaurantId,
            userId = entity.userId,
            status = OrderResponse.OrderStatus.valueOf(entity.status.name),
            items = entity.items.map {
                OrderResponse.OrderItem(
                    it.id,
                    it.productId
                )
            },
            total = entity.total,
            paymentId = entity.paymentId,
            deliveryAddressId = entity.deliveryAddressId
        )
    }
}
