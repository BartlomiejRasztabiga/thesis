package me.rasztabiga.thesis.order.domain.query.mapper

import me.rasztabiga.thesis.order.domain.command.event.OrderStartedEvent
import me.rasztabiga.thesis.order.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import java.math.BigDecimal

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
            deliveryAddressId = null
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
