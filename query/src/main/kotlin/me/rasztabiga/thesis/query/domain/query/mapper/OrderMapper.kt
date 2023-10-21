package me.rasztabiga.thesis.query.domain.query.mapper

import me.rasztabiga.thesis.query.domain.query.entity.CourierEntity
import me.rasztabiga.thesis.query.domain.query.entity.OrderEntity
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.Location
import me.rasztabiga.thesis.shared.adapter.`in`.rest.api.OrderResponse
import me.rasztabiga.thesis.shared.domain.command.event.OrderStartedEvent
import java.time.Instant

object OrderMapper {

    fun mapToEntity(event: OrderStartedEvent, restaurantLocation: Location): OrderEntity {
        return OrderEntity(
            id = event.orderId,
            restaurantId = event.restaurantId,
            restaurantLocation = restaurantLocation,
            userId = event.userId,
            status = OrderEntity.OrderStatus.valueOf(event.status.name),
            items = mutableMapOf(),
            total = null,
            productsTotal = null,
            paymentId = null,
            paymentSessionUrl = null,
            deliveryAddressId = null,
            deliveryLocation = null,
            courierId = null,
            deliveryFee = null,
            createdAt = Instant.now()
        )
    }

    fun mapToResponse(entity: OrderEntity, courier: CourierEntity?): OrderResponse {
        return OrderResponse(
            id = entity.id,
            restaurantId = entity.restaurantId,
            restaurantLocation = entity.restaurantLocation,
            userId = entity.userId,
            status = OrderResponse.OrderStatus.valueOf(entity.status.name),
            items = entity.items,
            total = entity.total,
            itemsTotal = entity.productsTotal,
            paymentId = entity.paymentId,
            paymentSessionUrl = entity.paymentSessionUrl,
            deliveryAddressId = entity.deliveryAddressId,
            deliveryLocation = entity.deliveryLocation,
            courierId = entity.courierId,
            courierLocation = courier?.location,
            deliveryFee = entity.deliveryFee,
            createdAt = entity.createdAt
        )
    }
}
